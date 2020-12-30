package com.pedrosantos.challenge.services.userdocument;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.internal.creation.MockSettingsImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.pedrosantos.challenge.entities.UserDocument;
import com.pedrosantos.challenge.entities.WordMatch;
import com.pedrosantos.challenge.repositories.UserDocumentRepository;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CreateUserDocumentServiceTest {

	@Mock
	private UserDocumentRepository repository;

	private CreateUserDocumentService createUserDocument;

	private UserDocument userDocument;

	@BeforeAll
	public void setUp() {

		MockSettings mockSettings = new MockSettingsImpl<UserDocument>().useConstructor(1, "title", "location", "user",
				new ArrayList<WordMatch>(), new Date());

		userDocument = mock(UserDocument.class, mockSettings);

		createUserDocument = mock(CreateUserDocumentService.class);
	}

	@Test
	public void createOneUserDocument_withMock_returnCreatedInstance() {
		// arrange
		given(repository.save(userDocument)).willReturn(userDocument);
		when(createUserDocument.insertOne(userDocument)).thenReturn(userDocument);

		// act
		UserDocument createdInstance = createUserDocument.insertOne(userDocument);

		// assert
		then(createUserDocument).should(times(1)).insertOne(userDocument);
		assertNotNull(createdInstance);
	}
}
