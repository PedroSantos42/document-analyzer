package com.pedrosantos.challenge.providers;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class AmazonS3Provider {

	@Autowired
	private AmazonS3 s3Client;

	@Value("${aws.s3.bucket}")
	private String bucketName;

	@Value("${storage.folder}")
	private String diskFolder;

	public String store(MultipartFile file) {
		String filePath = String.format("%s/%s", Paths.get(diskFolder), file.getOriginalFilename());

		PutObjectRequest request = new PutObjectRequest(bucketName, file.getOriginalFilename(), new File(filePath));

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType("plain/text");
		metadata.addUserMetadata("title", "someTitle");

		request.setMetadata(metadata);

		s3Client.putObject(request);

		URL s3ObjectUrl = s3Client.getUrl(bucketName, file.getOriginalFilename());
		String resourceUrl = String.format("%s%s", s3ObjectUrl.getHost(), s3ObjectUrl.getPath());

		return resourceUrl;
	}

	public List<String> loadAll() {
		ObjectListing result = s3Client.listObjects(bucketName);

		List<String> s3ObjectKeys = result.getObjectSummaries().stream().map(s3Object -> s3Object.getKey())
				.collect(Collectors.toList());

		List<String> s3ObjectURLs = s3ObjectKeys.stream()
				.map(objectKey -> String.format("https://%s.s3.amazonaws.com/%s", bucketName, objectKey))
				.collect(Collectors.toList());

		return s3ObjectURLs;
	}
}
