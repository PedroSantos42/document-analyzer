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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.entities.WordMatch;
import com.pedrosantos.challenge.exceptions.StorageFileNotFoundException;
import com.pedrosantos.challenge.providers.impl.analysis.AmazonTextractProvider;
import com.pedrosantos.challenge.providers.impl.storage.AmazonS3Provider;
import com.pedrosantos.challenge.providers.impl.storage.DiskStorageProvider;
import com.pedrosantos.challenge.services.userdocument.CreateUserDocumentService;

@RestController
@RequestMapping(value = "/")
public class FileUploadResource {

	private final AmazonS3Provider amazonStorage;

	private final AmazonTextractProvider amazonDocumentAnalyser;

	private final DiskStorageProvider diskStorage;

	private final CreateUserDocumentService createUserDocument;

	@Value("${aws.s3.url}")
	private String s3Url;

	@Autowired
	public FileUploadResource(DiskStorageProvider diskStorage, AmazonS3Provider amazonStorage,
			CreateUserDocumentService createUserDocument, AmazonTextractProvider amazonDocumentAnalyser) {
		this.diskStorage = diskStorage;
		this.amazonStorage = amazonStorage;
		this.createUserDocument = createUserDocument;
		this.amazonDocumentAnalyser = amazonDocumentAnalyser;
	}

	@GetMapping("/")
	public ResponseEntity<List<String>> listUploadedFiles(Model model) throws IOException {

		List<String> result = diskStorage.loadAll()
				.map(path -> MvcUriComponentsBuilder
						.fromMethodName(FileUploadResource.class, "serveFile", path.getFileName().toString()).build()
						.toUri()
						.toString())
				.collect(Collectors.toList());

		return ResponseEntity.ok().body(result);
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
		amazonStorage.store(file);

		List<String> wordsInDocument = amazonDocumentAnalyser.analyseDocument(fileName);

		List<WordMatch> matches = wordsInDocument.stream().distinct()
				.map(word -> getWordOccurrencesInList(word, wordsInDocument))
				.sorted((Comparator<WordMatch>) (word1, word2) -> word2.getQuantity().compareTo(word1.getQuantity()))
				.collect(Collectors.toList());

		UserDocument result = createUserDocument.execute(UserDocument.builder().title(fileName)
				.location(s3Url.concat("/")).matches(matches).user("Pedro Santos").createdAt(new Date()).build());

		return ResponseEntity.created(null).body(result);
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
