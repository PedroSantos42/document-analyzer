package com.pedrosantos.challenge.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.model.DocumentLocation;
import com.amazonaws.services.textract.model.GetDocumentAnalysisRequest;
import com.amazonaws.services.textract.model.GetDocumentAnalysisResult;
import com.amazonaws.services.textract.model.StartDocumentAnalysisRequest;
import com.amazonaws.services.textract.model.StartDocumentAnalysisResult;

@Service
public class AmazonTextractProvider {

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.s3.bucket}")
	private String bucket;

	@Value("${aws.textract.endpoint}")
	private String textractEndpoint;

	@Autowired
	private AmazonS3 s3client;

	@Autowired
	private AmazonTextract textractClient;

	public List<String> analyseDocument(String fileName) {

		// Get the document from S3
		S3Object s3object = s3client.getObject(bucket, fileName);

		StartDocumentAnalysisRequest startDocumentAnalysisRequest = new StartDocumentAnalysisRequest()
				.withFeatureTypes("TABLES", "FORMS").withDocumentLocation(
						new DocumentLocation().withS3Object(new com.amazonaws.services.textract.model.S3Object()
								.withName(s3object.getKey()).withBucket(s3object.getBucketName())));

		StartDocumentAnalysisResult analysisRequest = textractClient
				.startDocumentAnalysis(startDocumentAnalysisRequest);

		String jobId = analysisRequest.getJobId();

		Boolean isAnalysisInProgress = true;
		GetDocumentAnalysisRequest documentAnalysisRequest = new GetDocumentAnalysisRequest().withJobId(jobId);
		GetDocumentAnalysisResult analysisResult = new GetDocumentAnalysisResult();

		do {
			analysisResult = textractClient.getDocumentAnalysis(documentAnalysisRequest);

			isAnalysisInProgress = analysisResult.getJobStatus().equals("IN_PROGRESS");

			if (isAnalysisInProgress) {
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else
				isAnalysisInProgress = false;

		} while (isAnalysisInProgress);

		// get texts from analysis response
		List<String> textBlocks = analysisResult.getBlocks().stream().filter(block -> block.getText() != null)
				.map(block -> block.getText().trim().replaceAll("\\p{Punct}+$", "").replace(",", ""))
				.collect(Collectors.toList());

		// filter the textBlocks with blank spaces to another list
		List<String> textBlocksWithBlankSpaces = textBlocks.stream().filter(textBlock -> textBlock.contains(" "))
				.collect(Collectors.toList());

		// remove textBlocks with blank spaces
		textBlocks = textBlocks.stream().filter(textBlock -> !textBlock.contains(" ")).collect(Collectors.toList());

		List<String> separatedWordsFromTextBlocksWithBlankSpaces = new ArrayList<>();

		for (String text : textBlocksWithBlankSpaces)
			separatedWordsFromTextBlocksWithBlankSpaces.addAll(Arrays.asList(text.trim().split(" ")));

		textBlocks.addAll(separatedWordsFromTextBlocksWithBlankSpaces);

		textBlocks = textBlocks.stream().filter(textBlock -> !textBlock.trim().isEmpty())
				.map(textBlock -> textBlock.replaceAll("\\p{Punct}+$", "")).collect(Collectors.toList());

		return textBlocks;
	}

}
