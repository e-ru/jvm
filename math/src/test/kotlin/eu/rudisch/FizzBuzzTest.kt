package eu.rudisch

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class FizzBuzzTest {

	@Test
	fun processNumbersTest() {
		val fizzBuzz = FizzBuzz()
		assertEquals("1", fizzBuzz.processNumber(1))
		assertEquals("2", fizzBuzz.processNumber(2))
		assertEquals("Fizz", fizzBuzz.processNumber(3))
		assertEquals("Buzz", fizzBuzz.processNumber(5))
		assertEquals("Fizz", fizzBuzz.processNumber(6))
		assertEquals("Buzz", fizzBuzz.processNumber(10))
		assertEquals("FizzBuzz", fizzBuzz.processNumber(15))
		assertEquals("FizzBuzz", fizzBuzz.processNumber(30))
		assertEquals("1, 2, Fizz, Buzz, Fizz, Buzz, FizzBuzz, FizzBuzz",
				fizzBuzz.processList(listOf(1, 2, 3, 5, 6, 10, 15, 30)))
	}
}