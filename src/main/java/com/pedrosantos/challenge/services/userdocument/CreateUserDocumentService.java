package com.pedrosantos.challenge.services.userdocument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.repositories.UserDocumentRepository;

@Service
public class CreateUserDocumentService {

	@Autowired
	private UserDocumentRepository userDocumentRepository;

	public UserDocument execute(UserDocument userDocument) {

		UserDocument result = userDocumentRepository.save(userDocument);

		return result;
	}
}
