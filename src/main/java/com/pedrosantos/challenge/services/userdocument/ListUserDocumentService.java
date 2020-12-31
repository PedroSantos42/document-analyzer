package com.pedrosantos.challenge.services.userdocument;

import java.util.List;

import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.repositories.UserDocumentRepository;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ListUserDocumentService {
    
    private UserDocumentRepository repository;

    public List<UserDocument> findAll() {
        return repository.findAll();
    }
}
