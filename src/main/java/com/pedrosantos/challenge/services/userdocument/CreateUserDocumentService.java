package com.pedrosantos.challenge.services.userdocument;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.repositories.UserDocumentRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CreateUserDocumentService {

	private UserDocumentRepository repo;

	public UserDocument insertOne(UserDocument obj) {

		obj.setId(null);

		UserDocument result = repo.save(obj);

		return result;
	}

	public List<UserDocument> insertAll(List<UserDocument> objs) {

		for (UserDocument document : objs)
			document.setId(null);

		List<UserDocument> result = repo.saveAll(objs);

		return result;
	}
}
