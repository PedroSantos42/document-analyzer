package com.pedrosantos.challenge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;

@Configuration
public class TextractorConfig {

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.textract.endpoint}")
	private String textractEndpoint;

	@Bean
	public AmazonTextract textractClient() {
		EndpointConfiguration endpoint = new EndpointConfiguration(textractEndpoint, awsRegion);

		return AmazonTextractClientBuilder.standard().withEndpointConfiguration(endpoint).build();
	}
}
