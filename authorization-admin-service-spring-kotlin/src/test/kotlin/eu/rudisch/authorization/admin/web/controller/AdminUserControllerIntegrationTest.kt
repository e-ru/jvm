package eu.rudisch.authorization.admin.web.controller

import org.hamcrest.CoreMatchers.`is`
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

import eu.rudisch.authorization.admin.AuthorizationAdminApplication
import eu.rudisch.authorization.admin.service.UserService
import eu.rudisch.authorization.admin.web.controller.resource.UserRestRep

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AuthorizationAdminApplication::class])
@WebMvcTest(controllers = [AdminUserController::class])
class AdminUserControllerIntegrationTest {

	@Autowired
	private val mockMvc: MockMvc? = null

	@MockBean
	private val userService: UserService? = null

	@Test
	@WithMockUser(username = "eru", roles = ["oauth_admin", "oauth_viewer"])
	@Throws(Exception::class)
	fun shouldGetAllUserRepresentations() {
		val user = UserRestRep(0,
				"test_user",
				"123456",
				"123456",
				"a@b.c",
				enabled = true,
				accountExpired = false,
				credentialsExpired = false,
				accountLocked = false,
				roleNames = listOf("test_role_one"))

		val allUsers = listOf(user)

		given<List<UserRestRep>>(userService!!.getUserRepresentations()).willReturn(allUsers)

		mockMvc!!.perform(get("/admin/users")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk)
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath<String>("$[0].username", `is`<String>(user.username)))
	}
}