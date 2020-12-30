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

		// make the analysis asynchronously
		GetDocumentAnalysisResult analysisResult = StartAsyncDocumentAnalysis(s3object);

		// get texts from analysis result
		List<String> allWordsFromDocument = getWordsFromAnalysedDocument(analysisResult);

		return allWordsFromDocument;
	}

	private GetDocumentAnalysisResult StartAsyncDocumentAnalysis(S3Object s3object) {

		// make the analysis request
		StartDocumentAnalysisRequest startDocumentAnalysisRequest = new StartDocumentAnalysisRequest()
				.withFeatureTypes("TABLES", "FORMS").withDocumentLocation(
						new DocumentLocation().withS3Object(new com.amazonaws.services.textract.model.S3Object()
								.withName(s3object.getKey()).withBucket(s3object.getBucketName())));

		// start document analysis
		StartDocumentAnalysisResult analysisRequest = textractClient
				.startDocumentAnalysis(startDocumentAnalysisRequest);

		// get ID for future referring
		String analysisJobId = analysisRequest.getJobId();

		// make request and get the analysis results
		GetDocumentAnalysisRequest documentAnalysisRequest = new GetDocumentAnalysisRequest().withJobId(analysisJobId);
		GetDocumentAnalysisResult analysisResult = new GetDocumentAnalysisResult();

		// checking for analysis completion
		Boolean isAnalysisInProgress = true;
		do {
			analysisResult = textractClient.getDocumentAnalysis(documentAnalysisRequest);

			// verify the analysis status
			isAnalysisInProgress = analysisResult.getJobStatus().equals("IN_PROGRESS");

			// wait 3 seconds
			if (isAnalysisInProgress) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else
				isAnalysisInProgress = false;

		} while (isAnalysisInProgress);

		return analysisResult;
	}

	private List<String> getWordsFromAnalysedDocument(GetDocumentAnalysisResult analysedDocument) {

		List<String> textBlocks = new ArrayList<>();

		// get TextBlocks from Amazon Textract document analysis response
		// filtering blocks with empty spaces and removing punctuation
		textBlocks = analysedDocument.getBlocks().stream().filter(block -> block.getText() != null)
				.map(block -> block.getText().trim().replaceAll("\\p{Punct}+$", "").replace(",", ""))
				.collect(Collectors.toList());

		// filter the textBlocks with blank spaces to another list
		List<String> textBlocksWithBlankSpaces = textBlocks.stream().filter(textBlock -> textBlock.contains(" "))
				.collect(Collectors.toList());

		// remove textBlocks with blank spaces
		textBlocks = textBlocks.stream().filter(textBlock -> !textBlock.contains(" ")).collect(Collectors.toList());

		// removing blank spaces
		List<String> separatedWordsFromTextBlocksWithBlankSpaces = new ArrayList<>();
		for (String text : textBlocksWithBlankSpaces)
			separatedWordsFromTextBlocksWithBlankSpaces.addAll(Arrays.asList(text.trim().split(" ")));

		textBlocks.addAll(separatedWordsFromTextBlocksWithBlankSpaces);

		// process one more time for removing invalid characters
		textBlocks = textBlocks.stream().filter(textBlock -> !textBlock.trim().isEmpty())
				.map(textBlock -> textBlock.replaceAll("\\p{Punct}+$", "")).collect(Collectors.toList());

		return textBlocks;
	}

}
