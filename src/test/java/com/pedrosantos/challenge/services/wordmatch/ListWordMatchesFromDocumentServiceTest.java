package com.pedrosantos.challenge.services.wordmatch;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.pedrosantos.challenge.entities.WordMatch;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ListWordMatchesFromDocumentServiceTest {

	@InjectMocks
	private ListWordMatchesFromDocumentService listMatchesFromDocument;

	private List<String> mockText;

	@BeforeAll
	public void setUp() {
		mockText = new ArrayList<>();
		mockText = createMockList();
	}

	@Test
	private void createWordMatches_fromStringList_returnMatches() {
		// arrange
		// setup mock list

		// act
		List<WordMatch> matches = listMatchesFromDocument.execute(mockText);

		// assert
		assertNotNull(matches);
	}

	private List createMockList() {

		StringBuilder sampleText = new StringBuilder();

		sampleText.append(
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua ")
				.append("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat ")
				.append("Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur ")
				.append("Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum ");

		return Arrays.asList(sampleText.toString().split(" "));
	}
}
