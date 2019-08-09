package eu.rudisch.authorization.admin.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

import eu.rudisch.authorization.admin.model.Permission
import eu.rudisch.authorization.admin.model.Role

@DataJpaTest
class RoleRepositoryIntegrationTest {

	@Autowired
	private val entityManager: TestEntityManager? = null

	@Autowired
	private val roleRepository: RoleRepository? = null

	@Test
	fun shouldFindOneRole() {
		val role = Role(0, "test_role", listOf(Permission(0, "test_permission")))
		roleRepository!!.save(role)

		val roles = roleRepository.findByNames(listOf("test_role"))
		assertEquals(1, roles.size)

		roleRepository.delete(role)
	}

	@Test
	fun shouldFindTwoRole() {
		val permission = Permission(0, "test_permission")
		val role1 = Role(0, "test_role_one", listOf(permission))
		val role2 = Role(1, "test_role_two", listOf(permission))

		roleRepository!!.save(role1)
		roleRepository.save(role2)

		val roles = roleRepository!!.findByNames(listOf("test_role_one", "test_role_two"))
		assertEquals(2, roles.size)
	}

	@Test
	fun shouldFindRoleByName() {
		val role1 = Role(0, "test_role_one", listOf(Permission(0, "test_permission")))
		roleRepository!!.save(role1)

		val optRole = roleRepository.findByName("test_role_one")
		assertTrue(optRole.isPresent)
	}
}