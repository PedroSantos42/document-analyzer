package com.pedrosantos.challenge.services.userdocument;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.services.userdocument.interfaces.ICreateUserDocumentService;

@Service
public class CreateUserDocumentService implements ICreateUserDocumentService {
	
	List<UserDocument> users = new ArrayList<>();
	
	@Override
	public UserDocument create(UserDocument userDocument) {
		UserDocument user = 
				UserDocument
					.builder()
						.location(userDocument.getLocation())
						.title(userDocument.getTitle())
						.user(userDocument.getUser())
					.build();
		
		users.add(user);
		
		return user;
	}

	@Override
	public List<UserDocument> index() {
		// TODO Auto-generated method stub
		return users;
	}
}
