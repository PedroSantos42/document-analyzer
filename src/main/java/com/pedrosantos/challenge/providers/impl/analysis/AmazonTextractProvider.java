package com.pedrosantos.challenge.providers.impl.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.AnalyzeDocumentRequest;
import com.amazonaws.services.textract.model.AnalyzeDocumentResult;
import com.amazonaws.services.textract.model.Document;
import com.pedrosantos.challenge.entities.WordMatch;

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

	public List<String> analyseDocument(String fileName) {

		// Get the document from S3
		S3Object s3object = s3client.getObject(bucket, fileName);

		// Call AnalyzeDocument
		EndpointConfiguration endpoint = new EndpointConfiguration(textractEndpoint, awsRegion);

		AmazonTextract client = AmazonTextractClientBuilder.standard().withEndpointConfiguration(endpoint).build();

		AnalyzeDocumentRequest request = new AnalyzeDocumentRequest().withFeatureTypes("TABLES", "FORMS")
				.withDocument(new Document().withS3Object(new com.amazonaws.services.textract.model.S3Object()
						.withName(s3object.getKey()).withBucket(s3object.getBucketName())));

		AnalyzeDocumentResult result = client.analyzeDocument(request);

		List<String> textBlocks = result.getBlocks().stream().filter(block -> block.getText() != null)
				.map(block -> block.getText().replaceAll("\\p{Punct}+$", "").replace(",", ""))
				.collect(Collectors.toList());

		// filter the textBlocks with blank spaces to another list
		List<String> textBlocksWithBlankSpaces = textBlocks.stream().filter(textBlock -> textBlock.trim().contains(" "))
				.collect(Collectors.toList());

		// remove textBlocks with blank spaces
		textBlocks = textBlocks.stream().filter(textBlock -> !textBlock.trim().contains(" "))
				.collect(Collectors.toList());

		List<String> separatedWordsFromTextBlocksWithBlankSpaces = new ArrayList<>();

		for (String text : textBlocksWithBlankSpaces) {
			separatedWordsFromTextBlocksWithBlankSpaces.addAll(Arrays.asList(text.trim().split(" ")));
		}

		textBlocks.addAll(separatedWordsFromTextBlocksWithBlankSpaces);

		textBlocks = textBlocks.stream().filter(textBlock -> !textBlock.trim().isEmpty())
				.map(textBlock -> textBlock.replaceAll("\\p{Punct}+$", "")).collect(Collectors.toList());

		return textBlocks;
	}

}
