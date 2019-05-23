package eu.rudisch.users.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import eu.rudisch.users.business.HashHandler.HashType;

@ExtendWith(MockitoExtension.class)
class HashHandlerTest {

	private static final String PASSWORD = "test";
	private static final String HASH_SHA256 = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";

	@Test
	void generateHashShouldReturnSHA256() {
		HashHandler hashHandler = new HashHandler();

		String genHash = PASSWORD;
		genHash = hashHandler.generateHash(PASSWORD, HashType.SHA256);
		assertEquals(HASH_SHA256, genHash);
	}

	@Test
	void validateHash() {
		HashHandler hashHandler = new HashHandler();
		boolean valid = hashHandler.validate(PASSWORD, HASH_SHA256);
		assertTrue(valid);
	}

}
