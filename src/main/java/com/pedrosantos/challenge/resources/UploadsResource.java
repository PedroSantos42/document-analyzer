package com.pedrosantos.challenge.resources;

import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.entities.WordMatch;
import com.pedrosantos.challenge.providers.AmazonS3Provider;
import com.pedrosantos.challenge.providers.AmazonTextractProvider;
import com.pedrosantos.challenge.providers.DiskStorageProvider;
import com.pedrosantos.challenge.services.userdocument.CreateUserDocumentService;
import com.pedrosantos.challenge.services.wordmatch.CreateWordMatchService;
import com.pedrosantos.challenge.services.wordmatch.ListWordMatchesFromDocumentService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/uploads")
@AllArgsConstructor
public class UploadsResource {

	private DiskStorageProvider diskStorageProvider;

	private AmazonS3Provider amazonStorage;

	private AmazonTextractProvider amazonDocumentAnalyser;

	private ListWordMatchesFromDocumentService listMatchesFromDocument;

	private CreateUserDocumentService createUserDocument;

	private CreateWordMatchService createWordMatches;

	@GetMapping
	public ResponseEntity<List<String>> index() {

		// get list of S3 Object URLs
		List<String> resultFromAmazonStorage = amazonStorage.loadAll();

		return ResponseEntity.ok().body(resultFromAmazonStorage);
	}

	@PostMapping
	public ResponseEntity<Void> create(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "format") String format) {

		String fileName = file.getOriginalFilename();

		// save file in local storage
		diskStorageProvider.store(file);

		// put document in a S3 Bucket for further analysis
		String resourceUrl = amazonStorage.store(file);

		// analyze document with Amazon Textract
		List<String> wordsInDocument = amazonDocumentAnalyser.analyseDocument(fileName);

		// extract matches from the analyzed document
		List<WordMatch> matches = listMatchesFromDocument.execute(wordsInDocument);

		// persist Document in database
		UserDocument createdDocument = createUserDocument
				.insertOne(UserDocument.builder().title(fileName).location(String.format("https://%s", resourceUrl))
						.matches(matches).user("Pedro Santos").createdAt(new Date()).build());

		// set Document reference in the Matches
		for (WordMatch match : matches)
			match.setDocument(createdDocument);

		// persist Word Matches in database
		createWordMatches.insertAll(matches);

		return ResponseEntity.created(null).body(null);
	}
}
