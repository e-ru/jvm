package eu.rudisch

class NumberTheory {

	fun factorsOf(n: Int): List<Int> {
		var remainder = n
		var divisor = 2
		val factors = mutableListOf<Int>()
		while (remainder > 1) {
			while (remainder % divisor == 0) {
				factors.add(divisor)
				remainder /= divisor
			}
			divisor++
		}
		return factors;
	}

	fun kgv(n: Int, k: Int): Int {
		return n * k
	}

}