package eu.rudisch.authorizationserver.code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.rudisch.authorizationserver.AuthorizationServerApplication;
import eu.rudisch.authorizationserver.model.AuthUserDetail;
import eu.rudisch.authorizationserver.model.Permission;
import eu.rudisch.authorizationserver.model.Role;
import eu.rudisch.authorizationserver.model.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AuthorizationServerApplication.class)
public class JwtAuthorizationCodeServicesHelperServiceImplTest {

	@TestConfiguration
	static class JwtAuthorizationCodeServicesTestContextConfiguration {
		@Bean
		public DataSource dataSource() {
			return Mockito.mock(DataSource.class);
		}

		@Bean
		public JwtAuthorizationCodeServicesHelperServiceImpl jwtAuthorizationCodeServicesHelperServiceImpl() {
			return Mockito.mock(JwtAuthorizationCodeServicesHelperServiceImpl.class);
		}
	}

	@Autowired
	private JwtAuthorizationCodeServicesHelperServiceImpl jwtAuthorizationCodeServicesHelperServiceImpl;

	@MockBean
	private UserDetailsService userDetailsService;
	@MockBean
	private JdbcClientDetailsService jbcClientDetailsService;

	@MockBean
	private ClientDetails clientDetails;

	@Test
	public void shouldCreateOAuth2Authentication() {
		Permission p = new Permission();
		Role r = new Role();
		User u = new User();
		p.setName(JwtAuthorizationCodeServicesTest.PERMISSION);
		r.setName(JwtAuthorizationCodeServicesTest.ROLE);
		r.setPermissions(List.of(p));
		u.setUsername(JwtAuthorizationCodeServicesTest.USERNAME);
		u.setRoles(List.of(r));
		AuthUserDetail authUserDetail = new AuthUserDetail(u);

		Mockito.when(userDetailsService.loadUserByUsername(JwtAuthorizationCodeServicesTest.USERNAME))
				.thenReturn(authUserDetail);
		Mockito.when(jbcClientDetailsService.loadClientByClientId(JwtAuthorizationCodeServicesTest.CLIENT_ID))
				.thenReturn(clientDetails);
		Mockito.when(clientDetails.getResourceIds()).thenReturn(JwtAuthorizationCodeServicesTest.RESOURCE_IDS);

		OAuth2Authentication oAuth2Authentication = jwtAuthorizationCodeServicesHelperServiceImpl.oAuth2Authentication(
				JwtAuthorizationCodeServicesTest.CLIENT_ID,
				JwtAuthorizationCodeServicesTest.SCOPE, JwtAuthorizationCodeServicesTest.REDIRECT_URI,
				JwtAuthorizationCodeServicesTest.USERNAME);

		Set<String> authorities = oAuth2Authentication.getAuthorities().stream()
				.map(ga -> ga.getAuthority())
				.collect(Collectors.toSet());
		assertTrue(authorities.contains(JwtAuthorizationCodeServicesTest.ROLE));
		assertTrue(authorities.contains(JwtAuthorizationCodeServicesTest.PERMISSION));
		assertEquals(oAuth2Authentication.getOAuth2Request().getClientId(), JwtAuthorizationCodeServicesTest.CLIENT_ID);
		assertEquals(oAuth2Authentication.getOAuth2Request().getRedirectUri(),
				JwtAuthorizationCodeServicesTest.REDIRECT_URI);
		assertEquals(oAuth2Authentication.getOAuth2Request().getResourceIds(),
				JwtAuthorizationCodeServicesTest.RESOURCE_IDS);
		assertEquals(oAuth2Authentication.getUserAuthentication().getName(), JwtAuthorizationCodeServicesTest.USERNAME);

		assertNotNull(oAuth2Authentication);
	}
}
