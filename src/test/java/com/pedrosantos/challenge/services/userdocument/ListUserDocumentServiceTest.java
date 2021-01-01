package com.pedrosantos.challenge.services.userdocument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.repositories.UserDocumentRepository;
import com.pedrosantos.challenge.services.exceptions.ObjectNotFoundException;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ListUserDocumentServiceTest {

	@Mock
	private UserDocumentRepository repository;

	@InjectMocks
	private ListUserDocumentService listUserDocument;

	private UserDocument userDocument;

	private List<UserDocument> userDocuments;

	@BeforeEach
	public void setUp() {

		userDocuments = new ArrayList<UserDocument>();

		for (int iterator = 1; iterator <= 3; iterator++) {
			userDocument = UserDocument.builder().id(iterator).build();
			userDocuments.add(userDocument);
		}
	}

	@Test
	public void listAllUserDocuments_returnAllInstancesFromDatabase() {
		// arrange
		given(repository.findAll()).willReturn(userDocuments);

		// act
		List<UserDocument> listedInstances = listUserDocument.getAll();

		// assert
		then(repository).should(times(1)).findAll();
		assertNotNull(listedInstances);
		assertEquals(userDocuments, listedInstances);
	}

	@Test
	public void listOneUserDocument_byId_returnInstanceFromDatabase() {
		// arrange
		given(repository.findById(anyLong())).willReturn(Optional.of(userDocument));

		// act
		UserDocument listedInstance = listUserDocument.getOneById(anyLong());

		// assert
		assertNotNull(listedInstance);
		then(repository).should(atLeastOnce()).findById(anyLong());
		assertEquals(userDocument, listedInstance);
	}

	@Test
	public void listOneUserDocument_withInvalidId_throwException() {
		// arrange
		given(repository.findById(anyLong())).willReturn(Optional.empty());

		// act & assert
		assertThrows(ObjectNotFoundException.class, () -> {
			listUserDocument.getOneById(anyLong());
		});
	}
}
