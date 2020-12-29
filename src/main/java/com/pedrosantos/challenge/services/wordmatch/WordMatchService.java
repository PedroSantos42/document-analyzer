package com.pedrosantos.challenge.services.wordmatch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pedrosantos.challenge.entities.WordMatch;
import com.pedrosantos.challenge.repositories.WordMatchRepository;

@Service
public class WordMatchService {

	@Autowired
	private WordMatchRepository repo;

	public WordMatch insert(WordMatch obj) {

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
