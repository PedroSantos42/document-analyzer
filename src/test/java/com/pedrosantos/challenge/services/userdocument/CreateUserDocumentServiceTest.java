package com.pedrosantos.challenge.services.userdocument;

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

import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.repositories.UserDocumentRepository;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CreateUserDocumentServiceTest {

	@Mock
	private UserDocumentRepository repository;

	@InjectMocks
	private CreateUserDocumentService createUserDocument;

	private UserDocument userDocument;

	private List<UserDocument> userDocuments;

	@BeforeAll
	public void setUp() {

		userDocuments = new ArrayList<UserDocument>();

		for (int iterator = 1; iterator <= 3; iterator++) {
			userDocument = mock(UserDocument.class);
			userDocument.setId(iterator);
			userDocuments.add(userDocument);
		}
	}

	@Test
	public void createOneUserDocument_withMock_returnCreatedInstance() {
		// arrange
		given(repository.save(userDocument)).willReturn(userDocument);

		// act
		UserDocument createdInstance = createUserDocument.insertOne(userDocument);

		// assert
		then(repository).should(times(1)).save(userDocument);
		assertNotNull(createdInstance);
		assertTrue(createdInstance instanceof UserDocument);
	}

	@Test
	public void createMoreThanOneUserDocuments_withValidArgs_returnCreatedInstances() {
		// arrange
		given(repository.saveAll(userDocuments)).willReturn(userDocuments);

		// act
		List<UserDocument> createdInstances = createUserDocument.insertAll(userDocuments);

		// assert
		then(repository).should(times(1)).saveAll(userDocuments);
		assertNotNull(createdInstances);
	}
}
