package eu.rudisch.authorization.admin.service

import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.test.context.junit.jupiter.SpringExtension

import eu.rudisch.authorization.admin.model.Permission
import eu.rudisch.authorization.admin.model.Role
import eu.rudisch.authorization.admin.model.User
import eu.rudisch.authorization.admin.repository.RoleRepository
import eu.rudisch.authorization.admin.repository.UserRepository
import eu.rudisch.authorization.admin.web.controller.resource.UserRestRep

@ExtendWith(SpringExtension::class)
class UserServiceImplIntegrationTest {

	@TestConfiguration
	internal class UserServiceImplTestContextConfiguration {
		@Bean
		fun userService(): UserService {
			return UserServiceImpl()
		}
	}

	@Autowired
	private val userService: UserService? = null

	@MockBean
	private val userRepository: UserRepository? = null

	@MockBean
	private val roleRepository: RoleRepository? = null

	@BeforeEach
	fun setUp() {
		val role1 = Role(0, "test_role_one", listOf(Permission(0, "test_permission")))
		val role2 = Role(0, RoleRepository.ROLE_AUTH_ADMIN, listOf(Permission(0, "test_permission")))

		val user = User(0,
				"test_user",
				"123456",
				"a@b.c",
				enabled = true,
				accountNonExpired = false,
				credentialsNonExpired=false,
				accountNonLocked = false,
				roles = listOf(role1, role2) )

		Mockito.`when`(userRepository!!.findAll())
				.thenReturn(listOf(user))
		Mockito.`when`<User>(userRepository.getOne(user.id))
				.thenReturn(user)
		Mockito.`when`(roleRepository!!.findByNames(listOf("test_role_one")))
				.thenReturn(listOf(role1))
		Mockito.`when`(roleRepository.findByNames(listOf("test_role_one", RoleRepository.ROLE_AUTH_ADMIN)))
				.thenReturn(listOf(role1, role2))
	}

	@Order(1)
	@Test
	fun shouldFindAllUsers() {
		val userRepresentations = userService!!.getUserRepresentations()
		assertEquals(1, userRepresentations.size)
	}

	@Order(2)
	@Test
	fun shouldUpdateSelfUserWithIdOneAndIsNotAbleToChangeSelfProtectedProperties() {
		val rep = UserRestRep(0,
				"to update",
				"123456",
				"123456",
				"a@b.c",
				enabled = true,
				accountExpired = false,
				credentialsExpired = false,
				accountLocked = false,
				roleNames = listOf(RoleRepository.ROLE_AUTH_ADMIN))

		val updated = userService!!.updateUser(rep.id, rep, "test_user")
		assertEquals(updated.username, "test_user")
		assertEquals(updated.roleNames.size, rep.roleNames.size)
	}

	@Order(3)
	@Test
	fun shouldUpdateDifferentUserWithIdOneAndIsAbleToChangeSelfProtectedProperties() {
		val rep = UserRestRep(0,
				"to update",
				"123456",
				"123456",
				"a@b.c",
				enabled = true,
				accountExpired = false,
				credentialsExpired = false,
				accountLocked = false,
				roleNames = listOf(RoleRepository.ROLE_AUTH_ADMIN))

		val updated = userService!!.updateUser(rep.id, rep, "to update")
		assertEquals(updated.username, rep.username)
		assertEquals(updated.roleNames.size, rep.roleNames.size)
	}

	@Order(4)
	@Test
	fun shouldUpdateSelfUserWithIdOneAndNotRemoveAuthAdminRole() {
		val rep = UserRestRep(0,
				"to update",
				"123456",
				"123456",
				"a@b.c",
				enabled = true,
				accountExpired = false,
				credentialsExpired = false,
				accountLocked = false,
				roleNames = listOf("test_role_one"))

		val updated = userService!!.updateUser(rep.id, rep, "to update")
		assertEquals(updated.username, rep.username)
		assertEquals(updated.roleNames.size, 2)
	}
}