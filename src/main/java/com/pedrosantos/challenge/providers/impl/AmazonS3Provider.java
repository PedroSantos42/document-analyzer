package com.pedrosantos.challenge.providers.impl;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.pedrosantos.challenge.providers.interfaces.StorageProvider;

@Service
public class AmazonS3Provider implements StorageProvider {

	@Autowired
	private AmazonS3 s3Client;

	@Value("${s3.bucket}")
	private String bucketName;

	@Value("${storage.folder}")
	private String diskFolder;

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void store(MultipartFile file) {
		try {
			// Upload a file as a new object with ContentType and title specified.

			String filePath = String.format("%s/%s", Paths.get(diskFolder), file.getOriginalFilename());

			PutObjectRequest request = new PutObjectRequest(bucketName, file.getOriginalFilename(), new File(filePath));

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType("plain/text");
			metadata.addUserMetadata("title", "someTitle");

			request.setMetadata(metadata);

			s3Client.putObject(request);

		} catch (AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process
			// it, so it returned an error response.

			e.printStackTrace();
		} catch (SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client
			// couldn't parse the response from Amazon S3.

			e.printStackTrace();
		}
	}

	@Override
	public Stream<Path> loadAll() {
		// TODO Auto-generated method stub
		
		// list files in bucket and return as Stream<Path>
		return null;
	}

	@Override
	public Path load(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource loadAsResource(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

}
