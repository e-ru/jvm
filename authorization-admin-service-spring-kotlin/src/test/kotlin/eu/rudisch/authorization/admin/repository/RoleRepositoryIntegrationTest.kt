package eu.rudisch.authorization.admin.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.junit.jupiter.SpringExtension

import eu.rudisch.authorization.admin.model.Permission
import eu.rudisch.authorization.admin.model.Role

@ExtendWith(SpringExtension::class)
@DataJpaTest
class RoleRepositoryIntegrationTest {

	@Autowired
	private val entityManager: TestEntityManager? = null

	@Autowired
	private val roleRepository: RoleRepository? = null

	@Test
	fun shouldFindOneRole() {
		val role = Role(0, "test_role", listOf(Permission(0, "test_permission")))
		entityManager!!.persist(role)
		entityManager.flush()

		val roles = roleRepository!!.findByNames(listOf("test_role"))
		assertEquals(1, roles.size)

		entityManager.remove(role)
	}

	@Test
	fun shouldFindTwoRole() {
		val role1 = Role(0,"test_role_one",listOf(Permission(0, "test_permission")) )
		val role2 = Role(1,"test_role_two",listOf(Permission(0, "test_permission")))

		entityManager!!.persist(role1)
		entityManager.persist(role2)
		entityManager.flush()

		val roles = roleRepository!!.findByNames(listOf("test_role_one", "test_role_two"))
		assertEquals(2, roles.size)
	}

	@Test
	fun shouldFindRoleByName() {
		val role1 = Role(0,"test_role_one",listOf(Permission(0, "test_permission")))
		entityManager!!.persist(role1)
		entityManager.flush()

		val optRole = roleRepository!!.findByName("test_role_one")
		assertTrue(optRole.isPresent)
	}
}