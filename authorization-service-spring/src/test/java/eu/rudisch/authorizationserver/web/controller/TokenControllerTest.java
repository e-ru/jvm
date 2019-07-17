package eu.rudisch.authorizationserver.web.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import eu.rudisch.authorizationserver.AccessTokenHelper;
import eu.rudisch.authorizationserver.AuthorizationServerApplication;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AuthorizationServerApplication.class)
@ActiveProfiles("test")
public class TokenControllerTest {

	@Autowired
	JdbcTemplate jdbcTemplate;

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

	@Test
	public void shouldRevokeForeignRefreshTokenAsAdminUser() throws Exception {
		// create 3 different refresh tokens
		// create refreshToken for regular user rru
		AccessTokenHelper.obtainAccessTokenByAuthorizationCode(mockMvc, "rru", "rpass", "read_oauth");
		// create refreshToken for regular user rru2
		AccessTokenHelper.obtainAccessTokenByAuthorizationCode(mockMvc, "rru2", "r2pass", "read_oauth");
		// get access token for admin user eru
		final String accessToken = AccessTokenHelper.obtainAccessTokenByAuthorizationCode(mockMvc, "eru", "epass",
				"create_oauth read_oauth update_oauth delete_oauth");
		int count = jdbcTemplate.queryForList("SELECT * FROM oauth_refresh_token").size();
		assertEquals(3, count);

		// delete the 2 foreign token
		String url = "http://localhost/tokens/refreshTokens?username=";
		mockMvc.perform(delete(url + "rru")
				.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
		mockMvc.perform(delete(url + "rru2")
				.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
		count = jdbcTemplate.queryForList("SELECT * FROM oauth_refresh_token").size();
		assertEquals(1, count);

		// empty the oauth_refresh_token table
		mockMvc.perform(delete(url + "eru")
				.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
		count = jdbcTemplate.queryForList("SELECT * FROM oauth_refresh_token").size();
		assertEquals(0, count);
	}

	@Test
	public void shouldRevokeOwnRefreshTokenAsRegularUser_token_user_match_is_correct() throws Exception {
		// create refreshToken for regular user rru
		final String accessToken = AccessTokenHelper.obtainAccessTokenByAuthorizationCode(mockMvc, "rru", "rpass",
				"read_oauth");
		int count = jdbcTemplate.queryForList("SELECT * FROM oauth_refresh_token").size();
		assertEquals(1, count);

		// delete own refresh token
		String url = "http://localhost/tokens/refreshTokens?username=";
		mockMvc.perform(delete(url + "rru")
				.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
		count = jdbcTemplate.queryForList("SELECT * FROM oauth_refresh_token").size();
		assertEquals(0, count);
	}

	@Test
	public void shouldRevokeForeignRefreshTokenAsRegularUser_token_user_match_is_not_correct() throws Exception {
		// create refreshToken for regular user rru
		AccessTokenHelper.obtainAccessTokenByAuthorizationCode(mockMvc, "rru", "rpass", "read_oauth");
		// create refreshToken for regular user rru2
		final String accessToken = AccessTokenHelper.obtainAccessTokenByAuthorizationCode(mockMvc, "rru2", "r2pass",
				"read_oauth");
		int count = jdbcTemplate.queryForList("SELECT * FROM oauth_refresh_token").size();
		assertEquals(2, count);

		// delete foreign refresh token
		String url = "http://localhost/tokens/refreshTokens?username=";
		mockMvc.perform(delete(url + "rru")
				.header("Authorization", "Bearer " + accessToken));
//				.andExpect(status().isUnauthorized());
		count = jdbcTemplate.queryForList("SELECT * FROM oauth_refresh_token").size();
		assertEquals(2, count);
	}

}
