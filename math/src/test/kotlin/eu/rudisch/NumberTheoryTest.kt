package eu.rudisch

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class NumberTheoryTest {

	@Test
	fun factors() {
		val numberTheory = NumberTheory()
		assertEquals(emptyList<Int>(), numberTheory.factorsOf(1))
		assertEquals(listOf(2), numberTheory.factorsOf(2))
		assertEquals(listOf(3), numberTheory.factorsOf(3))
		assertEquals(listOf(2, 2), numberTheory.factorsOf(4))
		assertEquals(listOf(5), numberTheory.factorsOf(5))
		assertEquals(listOf(2, 3), numberTheory.factorsOf(6))
		assertEquals(listOf(7), numberTheory.factorsOf(7))
		assertEquals(listOf(2, 2, 2), numberTheory.factorsOf(8))
		assertEquals(listOf(3, 3), numberTheory.factorsOf(9))
		assertEquals(listOf(2, 5), numberTheory.factorsOf(10))
		assertEquals(listOf(11), numberTheory.factorsOf(11))
		assertEquals(listOf(2, 2, 3), numberTheory.factorsOf(12))
		assertEquals(listOf(13), numberTheory.factorsOf(13))
		assertEquals(listOf(2, 7), numberTheory.factorsOf(14))
		assertEquals(listOf(3, 5), numberTheory.factorsOf(15))
		assertEquals(listOf(2, 2, 2, 2), numberTheory.factorsOf(16))
		assertEquals(listOf(17), numberTheory.factorsOf(17))
		assertEquals(listOf(2, 3, 3), numberTheory.factorsOf(18))
		assertEquals(listOf(3, 3, 3), numberTheory.factorsOf(27))
	}

	//	@Test
	fun kgvTest() {
		val numberTheory = NumberTheory()
		assertEquals(2, numberTheory.kgv(1, 2))
		assertEquals(6, numberTheory.kgv(2, 3))
	}

}