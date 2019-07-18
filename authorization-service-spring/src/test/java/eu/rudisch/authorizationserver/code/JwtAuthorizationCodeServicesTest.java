package eu.rudisch.authorizationserver.code;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.KeyPair;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.rudisch.authorizationserver.AuthorizationServerApplication;
import eu.rudisch.authorizationserver.Utils;
import eu.rudisch.authorizationserver.config.CustomProperties;
import eu.rudisch.authorizationserver.model.AuthUserDetail;
import eu.rudisch.authorizationserver.model.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AuthorizationServerApplication.class)
@ActiveProfiles("test")
class JwtAuthorizationCodeServicesTest {

	@TestConfiguration
	static class UserServiceImplTestContextConfiguration {
		@Bean
		public DataSource dataSource() {
			return Mockito.mock(DataSource.class);
		}
	}

	@Autowired
	private CustomProperties customProperties;

	@MockBean
	private UserDetailsService userDetailsService;
	@MockBean
	private JdbcClientDetailsService jbcClientDetailsService;
	@MockBean
	private ClientDetails clientDetails;
	@MockBean
	private Authentication authentication;
	@MockBean
	private OAuth2Authentication oAuth2Authentication;

	public KeyPair genKeyPair() {
		return Utils.genKeyPair(customProperties.getKeyStorePath(), customProperties.getKeyStorePassword(),
				customProperties.getKeyStoreAlias());
	}

	@Test
	void createAuthorizationCode() {
		JwtAuthorizationCodeServices jwtAuthorizationCodeServices = new JwtAuthorizationCodeServices(userDetailsService,
				jbcClientDetailsService, genKeyPair());

		String userName = "test_user";
		String clientId = "test_clientId";
		Set<String> scope = Set.of("test_scope");
		Set<String> resourceIds = Set.of("test_resource");
		String redirectUri = "test_redirectUri";

		User user = new User();
		user.setUsername(userName);
		OAuth2Request oAuth2Request = new OAuth2Request(null, clientId, null, true, scope, resourceIds, redirectUri,
				null,
				null);
		AuthUserDetail authUserDetail = new AuthUserDetail(user);

		Mockito.when(oAuth2Authentication.getUserAuthentication()).thenReturn(authentication);
		Mockito.when(authentication.getPrincipal()).thenReturn(user);
		Mockito.when(oAuth2Authentication.getOAuth2Request()).thenReturn(oAuth2Request);

		Mockito.when(userDetailsService.loadUserByUsername(userName)).thenReturn(authUserDetail);
		Mockito.when(jbcClientDetailsService.loadClientByClientId(clientId)).thenReturn(clientDetails);
		Mockito.when(clientDetails.getResourceIds()).thenReturn(resourceIds);

		String code = jwtAuthorizationCodeServices.createAuthorizationCode(oAuth2Authentication);

		OAuth2Authentication ret = jwtAuthorizationCodeServices.consumeAuthorizationCode(code);

		assertNotNull(jwtAuthorizationCodeServices);
	}

}
