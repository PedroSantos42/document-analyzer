package com.pedrosantos.challenge.services.userdocument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.repositories.UserDocumentRepository;

@Service
public class UserDocumentService {

	@Autowired
	private UserDocumentRepository repo;

	public UserDocument insert(UserDocument obj) {

		UserDocument result = repo.save(obj);

		return result;
	}
}
