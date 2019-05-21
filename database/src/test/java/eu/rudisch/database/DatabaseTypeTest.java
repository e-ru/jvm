package eu.rudisch.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class DatabaseTypeTest {

	@Test
	void shouldHaveCharValueAndNotInteger() {
		DatabaseType databaseType = DatabaseType.CHAR;
		assertEquals(1, databaseType.getValue());
		assertNotEquals(4, databaseType.getValue());
	}

	@Test
	void shouldHaveIntegerValueAndNotChar() {
		DatabaseType databaseType = DatabaseType.INTEGER;
		assertEquals(4, databaseType.getValue());
		assertNotEquals(1, databaseType.getValue());
	}

}
