package com.pedrosantos.challenge.services.userdocument;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.repositories.UserDocumentRepository;
import com.pedrosantos.challenge.services.exceptions.ObjectNotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ListUserDocumentService {

	private UserDocumentRepository repository;

	public List<UserDocument> getAll() {
		return repository.findAll();
	}

	public UserDocument getOneById(long id) {
		Optional<UserDocument> userDocument = repository.findById(id);

		return userDocument.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! ID: " + id + ", Tipo: " + UserDocument.class.getSimpleName()));
	}

}
