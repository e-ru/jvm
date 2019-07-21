package eu.rudisch.authorizationserver.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.rudisch.authorizationserver.AuthorizationServerApplication;
import eu.rudisch.authorizationserver.code.CodeAuthentication;
import eu.rudisch.authorizationserver.model.AuthUserDetail;
import eu.rudisch.authorizationserver.model.Permission;
import eu.rudisch.authorizationserver.model.Role;
import eu.rudisch.authorizationserver.model.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AuthorizationServerApplication.class)
class CustomJwtAccessTokenConverterTest {

	static final String ACCESS_TOKEN_VALUE = "test_access_token_value";

	static final String USERNAME = "test_user";
	static final String ROLE = "test_role";
	static final String PERMISSION = "test_permission";
	static final String CLIENT_ID = "test_clientId";
	static final Set<String> SCOPE = Set.of("test_scope");
	static final Set<String> RESOURCE_IDS = Set.of("test_resource");
	static final String REDIRECT_URI = "test_redirectUri";

	@TestConfiguration
	static class CustomJwtAccessTokenConverterContextConfiguration {
		@Bean
		public DataSource dataSource() {
			return Mockito.mock(DataSource.class);
		}

		@Bean
		public CustomJwtAccessTokenConverter customJwtAccessTokenConverter() {
			return Mockito.mock(CustomJwtAccessTokenConverter.class);
		}
	}

	@Autowired
	private CustomJwtAccessTokenConverter customJwtAccessTokenConverter;

	@MockBean
	private JdbcClientDetailsService jdbcClientDetailsService;
	@MockBean
	private ClientDetails clientDetails;

	private static Date accessTokenExp = new Date(System.currentTimeMillis() + 3600 * 1000);
	private static OAuth2Authentication oAuth2Authentication;
	private static DefaultOAuth2AccessToken oAuth2AccessToken;

	private JsonParser objectMapper = JsonParserFactory.create();

	@SuppressWarnings("unchecked")
	static Set<String> scope(Object permissions) {
		return ((List<String>) permissions).stream()
				.collect(Collectors.toSet());
	}

	@BeforeAll
	public static void init() {
		oAuth2AccessToken = new DefaultOAuth2AccessToken(ACCESS_TOKEN_VALUE);
		oAuth2AccessToken.setExpiration(accessTokenExp);
		oAuth2AccessToken.setScope(SCOPE);

		Permission p = new Permission();
		Role r = new Role();
		User u = new User();
		p.setName(PERMISSION);
		r.setName(ROLE);
		r.setPermissions(List.of(p));
		u.setUsername(USERNAME);
		u.setRoles(List.of(r));
		AuthUserDetail authUserDetail = new AuthUserDetail(u);

		oAuth2Authentication = new OAuth2Authentication(
				new OAuth2Request(null, CLIENT_ID, null, true, SCOPE, RESOURCE_IDS, REDIRECT_URI,
						null, null),
				new CodeAuthentication(authUserDetail));
	}

	@Test
	void test_enhance() {
		Mockito.when(jdbcClientDetailsService.loadClientByClientId(CLIENT_ID))
				.thenReturn(clientDetails);
		Mockito.when(clientDetails.getRefreshTokenValiditySeconds())
				.thenReturn(10000);

		OAuth2AccessToken result = customJwtAccessTokenConverter.enhance(oAuth2AccessToken, oAuth2Authentication);

		assertTrue(result.getScope().contains("test_scope"));
		assertNotNull(result.getRefreshToken());
		assertEquals(accessTokenExp.getTime(), result.getExpiration().getTime());

		Jwt jwtCode = JwtHelper.decode(result.getValue());
		String claimsStr = jwtCode.getClaims();
		Map<String, Object> claims = objectMapper.parseMap(claimsStr);

		String username = (String) claims.get("user_name");
		String clientId = (String) claims.get("client_id");
		String jti = (String) claims.get(JwtAccessTokenConverter.TOKEN_ID);
		int exp = (int) claims.get("exp");
		Set<String> aud = scope(claims.get("aud"));
		Set<String> scope = scope(claims.get("scope"));
		Set<String> authorities = scope(claims.get("authorities"));

		assertEquals(USERNAME, username);
		assertEquals(CLIENT_ID, clientId);
		assertEquals(ACCESS_TOKEN_VALUE, jti);
		assertEquals(accessTokenExp.getTime() / 1000, exp);
		assertTrue(aud.contains("test_resource"));
		assertTrue(scope.contains("test_scope"));
		assertTrue(authorities.contains(PERMISSION));
		assertTrue(authorities.contains(ROLE));
	}

}
