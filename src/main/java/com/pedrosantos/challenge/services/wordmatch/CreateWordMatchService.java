package com.pedrosantos.challenge.services.wordmatch;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pedrosantos.challenge.entities.WordMatch;
import com.pedrosantos.challenge.repositories.WordMatchRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CreateWordMatchService {

	private WordMatchRepository repo;

	public WordMatch insertOne(WordMatch obj) {

		obj.setId(null);

		WordMatch result = repo.save(obj);

		return result;
	}

	public List<WordMatch> insertAll(List<WordMatch> objs) {

		for (WordMatch match : objs)
			match.setId(null);

		List<WordMatch> result = repo.saveAll(objs);

		return result;
	}
}
