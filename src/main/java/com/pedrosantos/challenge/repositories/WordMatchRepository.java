package com.pedrosantos.challenge.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedrosantos.challenge.entities.WordMatch;

public interface WordMatchRepository extends JpaRepository<WordMatch, Long> {

}
