package eu.rudisch

import java.util.stream.Collectors

class FizzBuzz {
	fun processNumber(number: Int): String {
		if (number % 3 == 0 && number % 5 == 0)
			return "FizzBuzz"
		if (number % 3 == 0)
			return "Fizz"
		if (number % 5 == 0)
			return "Buzz"
		return number.toString()
	}

	fun processList(numbers: List<Int>): String {
		return numbers.stream()
				.map(this::processNumber)
				.collect(Collectors.joining(", "))
	}


}