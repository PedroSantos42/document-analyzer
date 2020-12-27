package com.pedrosantos.challenge.services.userdocument.interfaces;

import java.util.List;

import com.pedrosantos.challenge.entities.UserDocument;

public interface ICreateUserDocumentService {
	List<UserDocument> index();
	UserDocument create(UserDocument userDocument);
}
