package com.pedrosantos.challenge.providers.impl.analysis;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
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

	@Value("${s3.bucket}")
	private String bucket;

	// The S3 bucket and document
	// tornar informação dinâmica
	String document = "CV_pictures.png";

	AmazonS3 s3client = AmazonS3ClientBuilder.standard()
			.withEndpointConfiguration(new EndpointConfiguration("https://s3.amazonaws.com", "us-east-1")).build();

//	@Override
	public List<WordMatch> analyseDocument() {

		// Get the document from S3
		S3Object s3object = s3client.getObject(bucket, document);

		// Call AnalyzeDocument
		EndpointConfiguration endpoint = new EndpointConfiguration("https://textract.us-east-1.amazonaws.com",
				awsRegion);

		AmazonTextract client = AmazonTextractClientBuilder.standard().withEndpointConfiguration(endpoint).build();

		AnalyzeDocumentRequest request = new AnalyzeDocumentRequest().withFeatureTypes("TABLES", "FORMS")
				.withDocument(new Document().withS3Object(new com.amazonaws.services.textract.model.S3Object()
						.withName(s3object.getKey()).withBucket(s3object.getBucketName())));

		AnalyzeDocumentResult result = client.analyzeDocument(request);

		List<String> textBlocks = result.getBlocks().stream().filter(block -> block.getText() != null)
				.map(block -> block.getText()).collect(Collectors.toList());

		// remover caracteres especiais e calcular a ocorrência das palavras

		for (String text : textBlocks) {
			System.out.println(text);
			System.out.println("##");
		}

		return null;
	}

}
