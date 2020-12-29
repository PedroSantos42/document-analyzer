package com.pedrosantos.challenge.resources;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.entities.WordMatch;
import com.pedrosantos.challenge.exceptions.StorageFileNotFoundException;
import com.pedrosantos.challenge.providers.AmazonS3Provider;
import com.pedrosantos.challenge.providers.AmazonTextractProvider;
import com.pedrosantos.challenge.providers.DiskStorageProvider;
import com.pedrosantos.challenge.services.userdocument.UserDocumentService;
import com.pedrosantos.challenge.services.wordmatch.WordMatchService;

@RestController
@RequestMapping(value = "/")
public class FileUploadResource {

	private final AmazonS3Provider amazonStorage;

	private final AmazonTextractProvider amazonDocumentAnalyser;

	private final DiskStorageProvider diskStorage;

	private final UserDocumentService createUserDocument;

	private final WordMatchService createWordMatch;

	@Value("${aws.s3.url}")
	private String s3Url;

	@Autowired
	public FileUploadResource(DiskStorageProvider diskStorage, AmazonS3Provider amazonStorage,
			UserDocumentService createUserDocument, AmazonTextractProvider amazonDocumentAnalyser,
			WordMatchService createWordMatch) {
		this.diskStorage = diskStorage;
		this.amazonStorage = amazonStorage;
		this.createUserDocument = createUserDocument;
		this.amazonDocumentAnalyser = amazonDocumentAnalyser;
		this.createWordMatch = createWordMatch;
	}

	@GetMapping("/")
	public ResponseEntity<List<String>> listUploadedFiles(Model model) throws IOException {

		List<String> resultFromAmazonStorage = amazonStorage.loadAll();

		return ResponseEntity.ok().body(resultFromAmazonStorage);
	}

	@GetMapping("/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = diskStorage.loadAsResource(filename);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@PostMapping("/")
	public ResponseEntity<UserDocument> handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		String fileName = file.getOriginalFilename();

		// armazena no disco local
		diskStorage.store(file);

		// deve retornar URL de acesso
		String resourceUrl = amazonStorage.store(file);

		List<String> wordsInDocument = amazonDocumentAnalyser.analyseDocument(fileName);

		List<WordMatch> matches = wordsInDocument.stream().distinct()
				.map(word -> getWordOccurrencesInList(word, wordsInDocument))
				.sorted((Comparator<WordMatch>) (word1, word2) -> word2.getQuantity().compareTo(word1.getQuantity()))
				.collect(Collectors.toList());

		UserDocument createdDocument = UserDocument.builder().build();

		createdDocument = createUserDocument
				.insert(UserDocument.builder().title(fileName).location(String.format("https://%s", resourceUrl))
						.matches(matches).user("Pedro Santos").createdAt(new Date()).build());

		for (WordMatch match : matches)
			match.setDocument(createdDocument);

		createWordMatch.insertAll(matches);

		return ResponseEntity.created(null).body(createdDocument);
	}

	private WordMatch getWordOccurrencesInList(String word, List<String> wordList) {

		int count = -1;

		for (String wordInTheList : wordList)
			if (word.equals(wordInTheList))
				count++;

		WordMatch match = WordMatch.builder().word(word).quantity(count).build();

		return match;
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
