package com.pedrosantos.challenge.providers.impl.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.pedrosantos.challenge.config.StorageProperties;
import com.pedrosantos.challenge.exceptions.StorageException;
import com.pedrosantos.challenge.exceptions.StorageFileNotFoundException;
import com.pedrosantos.challenge.providers.interfaces.StorageProvider;

@Service
public class DiskStorageProvider implements StorageProvider {

	private final Path rootLocation;

	@Autowired
	public DiskStorageProvider(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}

			Path destinationFile = this.rootLocation.resolve(Paths.get(file.getOriginalFilename())).normalize()
					.toAbsolutePath();
			
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				// This is a security check
				throw new StorageException("Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(this.rootLocation::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file: " + filename);

			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		try {
			Boolean isFolderEmpty = isEmpty(rootLocation);

			if (isFolderEmpty) {
				FileSystemUtils.deleteRecursively(rootLocation.toFile());
			} else {
				System.out.println("Folder could not be deleted, check if it's empty");
			}
		} catch (IOException e) {
			throw new StorageException("Could not delete storage", e);
		}
	}

	@Override
	public void init() {
		Boolean isFolderCreated = Files.exists(rootLocation);

		if (!isFolderCreated) {
			try {
				Files.createDirectories(rootLocation);
			} catch (IOException e) {
				throw new StorageException("Could not initialize storage", e);
			}
		}
	}

	private boolean isEmpty(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
				return !directory.iterator().hasNext();
			}
		}

		return false;
	}
}