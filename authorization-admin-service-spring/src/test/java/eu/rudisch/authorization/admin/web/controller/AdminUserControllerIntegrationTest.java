package eu.rudisch.authorization.admin.web.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import eu.rudisch.authorization.admin.AuthorizationAdminApplication;
import eu.rudisch.authorization.admin.service.UserService;
import eu.rudisch.authorization.admin.web.controller.resource.UserRestRep;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AuthorizationAdminApplication.class)
@WebMvcTest(controllers = AdminUserController.class)
public class AdminUserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Test
	@WithMockUser(username = "eru", roles = { "oauth_admin", "oauth_viewer" })
	public void shouldGetAllUserRepresentations() throws Exception {
		UserRestRep user = new UserRestRep();
		user.setUsername("test_user");

		List<UserRestRep> allUsers = List.of(user);

		given(userService.getUserRepresentations()).willReturn(allUsers);

		mockMvc.perform(get("/admin/users")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].username", is(user.getUsername())));
	}

}
