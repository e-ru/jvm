package eu.rudisch.web.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import eu.rudisch.authorizationadmin.service.UserService;
import eu.rudisch.web.AccessTokenHelper;
import eu.rudisch.web.controller.resource.UserRestRep;

//@RunWith(SpringRunner.class)
//@WebAppConfiguration
//@SpringBootTest(classes = AuthorizationAdminApplication.class)
//@ActiveProfiles("test")
public class AdminUserControllerIntegrationTest {

	// https://www.baeldung.com/spring-boot-testing

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain)
				.build();
	}

	@MockBean
	private UserService userService;

	@Test
	public void shouldGetAllUserRepresentations() throws Exception {
		UserRestRep user = new UserRestRep();
		user.setUsername("test_user");

		List<UserRestRep> allUsers = List.of(user);

		given(userService.getUserRepresentations()).willReturn(allUsers);

		String accessToken = AccessTokenHelper.obtainAccessTokenByAuthorizationCode(mockMvc, "rru", "rpass",
				"read_oauth");

		mockMvc.perform(get("http://localhost/admin/users")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].username", is(user.getUsername())));
	}

}
