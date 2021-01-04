package com.pedrosantos.challenge.resources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pedrosantos.challenge.dtos.ListUserDocumentDTO;
import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.entities.WordMatch;
import com.pedrosantos.challenge.providers.AmazonS3Provider;
import com.pedrosantos.challenge.providers.AmazonTextractProvider;
import com.pedrosantos.challenge.providers.DiskStorageProvider;
import com.pedrosantos.challenge.providers.SpreadsheetConversionProvider;
import com.pedrosantos.challenge.services.userdocument.CreateUserDocumentService;
import com.pedrosantos.challenge.services.userdocument.ListUserDocumentService;
import com.pedrosantos.challenge.services.wordmatch.CreateWordMatchService;
import com.pedrosantos.challenge.services.wordmatch.ListWordMatchesFromDocumentService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/files")
@AllArgsConstructor
public class FilesResource {

	private DiskStorageProvider diskStorageProvider;

	private AmazonS3Provider amazonStorage;

	private AmazonTextractProvider amazonDocumentAnalyzer;

	private ListWordMatchesFromDocumentService listMatchesFromDocument;

	private CreateUserDocumentService createUserDocument;

	private ListUserDocumentService listUserDocument;

	private CreateWordMatchService createWordMatches;

	private SpreadsheetConversionProvider spreadsheetConverter;

	@GetMapping(value = "/teste")
	public ResponseEntity<String> download() {

		WordMatch match = WordMatch.builder().word("teste").quantity(2).build();
		WordMatch match2 = WordMatch.builder().word("computador").quantity(4).build();
		WordMatch match3 = WordMatch.builder().word("casa").quantity(9).build();

		List<WordMatch> matches = new ArrayList<>();

		matches.addAll(Arrays.asList(match, match2, match3));

		try {
			spreadsheetConverter.create(matches, String.format("%s.xlsx", "planilha"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok("Sucesso!");
	}

	@GetMapping
	public ResponseEntity<List<ListUserDocumentDTO>> index() {

		// get list of documents from database
		List<UserDocument> userDocuments = listUserDocument.getAll();

		// convert listed records into DTO response format
		List<ListUserDocumentDTO> userDocumentsDTO = userDocuments.stream()
				.map(document -> new ListUserDocumentDTO(document)).collect(Collectors.toList());

		return ResponseEntity.ok().body(userDocumentsDTO);
	}

	@PostMapping
	public ResponseEntity<Void> create(@RequestParam("file") MultipartFile file) {

		String fileName = file.getOriginalFilename();

		if (!isFileInAcceptedFormat(fileName)) {
			return ResponseEntity.unprocessableEntity().body(null);
		}

		// save file in local storage
		diskStorageProvider.store(file);

		// put document in a S3 Bucket for further analysis
		String fileResourceUrl = amazonStorage.store(file);

		// analyze document with Amazon Textract
		List<String> wordsInDocument = amazonDocumentAnalyzer.analyseDocument(fileName);

		// extract matches from the analyzed document
		List<WordMatch> matches = listMatchesFromDocument.execute(wordsInDocument);

		// make and upload the excel file
		try {
			String excelFilePath = spreadsheetConverter.create(matches,
					String.format("%s.xlsx", fileName.substring(0, fileName.indexOf("."))));

			// create file from excel path
			File excelFile = new File(excelFilePath);

			// upload file to s3 bucket
			fileResourceUrl = amazonStorage.store(excelFile);

		} catch (IOException e) {
			return ResponseEntity.badRequest().body(null);
		}

		// persist Document in database
		UserDocument createdDocument = createUserDocument
				.insertOne(UserDocument.builder().title(fileName).location(String.format("https://%s", fileResourceUrl))
						.matches(matches).user("John Doe").createdAt(new Date()).build());

		// set Document reference in the Matches
		for (WordMatch match : matches)
			match.setDocument(createdDocument);

		// persist Word Matches in database
		createWordMatches.insertAll(matches);

		return ResponseEntity.created(null).body(null);
	}

	private boolean isFileInAcceptedFormat(String fileName) {
		boolean isFileAccepted = false;

		isFileAccepted = 
				fileName.endsWith("pdf") || 
				fileName.endsWith("png") || 
				fileName.endsWith("jpg") || 
				fileName.endsWith("jpeg");
		
		return isFileAccepted;
	}
}
