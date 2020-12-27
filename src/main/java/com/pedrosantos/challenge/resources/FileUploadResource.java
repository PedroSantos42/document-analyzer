package com.pedrosantos.challenge.resources;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.pedrosantos.challenge.exceptions.StorageFileNotFoundException;
import com.pedrosantos.challenge.providers.impl.AmazonS3Provider;
import com.pedrosantos.challenge.providers.impl.DiskStorageProvider;

@RestController
@RequestMapping(value = "/")
public class FileUploadResource {

	private final AmazonS3Provider amazonStorage;

	private final DiskStorageProvider diskStorage;

	@Autowired
	public FileUploadResource(DiskStorageProvider diskStorage, AmazonS3Provider amazonStorage) {
		this.diskStorage = diskStorage;
		this.amazonStorage = amazonStorage;
	}

	@GetMapping("/")
	public ResponseEntity<List<String>> listUploadedFiles(Model model) throws IOException {

		List<String> result = diskStorage.loadAll()
				.map(path -> MvcUriComponentsBuilder
						.fromMethodName(FileUploadResource.class, "serveFile", path.getFileName().toString()).build()
						.toUri().toString())
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
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		// adicionar try catchs e tratamentos de erros
		
		diskStorage.store(file);
		
//		amazonStorage.store(file);

		return ResponseEntity.created(null).body("You successfully uploaded " + file.getOriginalFilename() + "!");
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
