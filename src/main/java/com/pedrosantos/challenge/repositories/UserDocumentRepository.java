package com.pedrosantos.challenge.repositories;

import com.pedrosantos.challenge.entities.UserDocument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocument, Long> {

}
