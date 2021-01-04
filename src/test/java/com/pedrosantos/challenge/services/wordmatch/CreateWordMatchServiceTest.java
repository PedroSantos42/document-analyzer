package com.pedrosantos.challenge.services.wordmatch;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.pedrosantos.challenge.entities.WordMatch;
import com.pedrosantos.challenge.repositories.WordMatchRepository;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CreateWordMatchServiceTest {

	@Mock
	private WordMatchRepository repository;

	@InjectMocks
	private CreateWordMatchService createWordMatch;

	private WordMatch wordMatch;

	private List<WordMatch> wordMatches;

	@BeforeAll
	public void setUp() {

		wordMatches = new ArrayList<WordMatch>();

		for (int iterator = 1; iterator <= 3; iterator++) {
			wordMatch = mock(WordMatch.class);
			wordMatch.setId(iterator);
			wordMatches.add(wordMatch);
		}
	}

	@Test
	public void createOneWordMatch_withMock_returnCreatedInstance() {
		// arrange
		given(repository.save(wordMatch)).willReturn(wordMatch);

		// act
		WordMatch createdInstance = createWordMatch.insertOne(wordMatch);

		// assert
		then(repository).should(times(1)).save(wordMatch);
		assertNotNull(createdInstance);
		assertTrue(createdInstance instanceof WordMatch);
	}

	@Test
	public void createMoreThanOneWordMatches_withValidArgs_returnCreatedInstances() {
		// arrange
		given(repository.saveAll(wordMatches)).willReturn(wordMatches);

		// act
		List<WordMatch> createdInstances = createWordMatch.insertAll(wordMatches);

		// assert
		then(repository).should(times(1)).saveAll(wordMatches);
		assertNotNull(createdInstances);
	}
}
