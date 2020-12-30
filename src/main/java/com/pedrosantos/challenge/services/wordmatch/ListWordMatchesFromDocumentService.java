package com.pedrosantos.challenge.services.wordmatch;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pedrosantos.challenge.entities.WordMatch;

@Service
public class ListWordMatchesFromDocumentService {

	public List<WordMatch> execute(List<String> wordsInDocument) {

		List<WordMatch> matches = wordsInDocument.stream().distinct()
				.map(word -> getWordOccurrencesInList(word, wordsInDocument))
				.sorted((Comparator<WordMatch>) (word1, word2) -> word2.getQuantity().compareTo(word1.getQuantity()))
				.collect(Collectors.toList());

		return matches;
	}

	private WordMatch getWordOccurrencesInList(String word, List<String> wordList) {

		int count = -1;

		for (String wordInTheList : wordList)
			if (word.equals(wordInTheList))
				count++;

		WordMatch match = WordMatch.builder().word(word).quantity(count).build();

		return match;
	}
}
