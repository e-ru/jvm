package eu.rudisch.authorizationserver.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.rudisch.authorizationserver.AuthorizationServerApplication;
import eu.rudisch.authorizationserver.code.CodeAuthentication;
import eu.rudisch.authorizationserver.model.AuthUserDetail;
import eu.rudisch.authorizationserver.model.Permission;
import eu.rudisch.authorizationserver.model.Role;
import eu.rudisch.authorizationserver.model.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AuthorizationServerApplication.class)
class CustomJdbcTokenStoreTest {

	static final String USERNAME = "test_user";
	static final String ROLE = "test_role";
	static final String PERMISSION = "test_permission";
	static final String CLIENT_ID_1 = "test_clientId_1";
	static final String CLIENT_ID_2 = "test_clientId_2";
	static final Set<String> SCOPE = Set.of("test_scope");
	static final Set<String> RESOURCE_IDS = Set.of("test_resource");
	static final String REDIRECT_URI = "test_redirectUri";

	@TestConfiguration
	static class CustomJdbcTokenStoreContextConfiguration {
		@Bean
		public DataSource dataSource() {
			return Mockito.mock(DataSource.class);
		}
	}

	@Autowired
	DataSource dataSource;

	private static OAuth2Authentication oAuth2Authentication1, oAuth2Authentication2;

	private static OAuth2RefreshToken refreshToken1, refreshToken2;

	private static OAuth2Authentication oAuth2Authentication(String clientId, AuthUserDetail authUserDetail) {
		return new OAuth2Authentication(
				new OAuth2Request(null, clientId, null, true, SCOPE, RESOURCE_IDS, REDIRECT_URI,
						null, null),
				new CodeAuthentication(authUserDetail));
	}

	@BeforeAll
	public static void init() {
		Permission p = new Permission();
		Role r = new Role();
		User u = new User();
		p.setName(PERMISSION);
		r.setName(ROLE);
		r.setPermissions(List.of(p));
		u.setUsername(USERNAME);
		u.setRoles(List.of(r));
		AuthUserDetail authUserDetail = new AuthUserDetail(u);

		oAuth2Authentication1 = oAuth2Authentication(CLIENT_ID_1, authUserDetail);
		oAuth2Authentication2 = oAuth2Authentication(CLIENT_ID_2, authUserDetail);

		refreshToken1 = new DefaultExpiringOAuth2RefreshToken(UUID.randomUUID().toString(),
				new Date(System.currentTimeMillis() + (10000L * 1000L)));
		refreshToken2 = new DefaultExpiringOAuth2RefreshToken(UUID.randomUUID().toString(),
				new Date(System.currentTimeMillis() + (10000L * 1000L)));
	}

	@Test
	void shouldInsertRefreshTokenAndAuthentication() {
		CustomJdbcTokenStore customJdbcTokenStore = new CustomJdbcTokenStore(dataSource);

		OAuth2RefreshToken rt = customJdbcTokenStore.readRefreshToken(refreshToken1.getValue());

		assertNull(rt);

		customJdbcTokenStore.storeRefreshToken(refreshToken1, oAuth2Authentication1);

		rt = customJdbcTokenStore.readRefreshToken(refreshToken1.getValue());

		assertNotNull(rt);
		assertEquals(refreshToken1.getValue(), rt.getValue());

		// cleanUp
		customJdbcTokenStore.removeRefreshTokenByUsername(USERNAME);
	}

	@Test
	void shouldDeleteByUsername() {
		CustomJdbcTokenStore customJdbcTokenStore = new CustomJdbcTokenStore(dataSource);

		customJdbcTokenStore.storeRefreshToken(refreshToken1, oAuth2Authentication1);
		customJdbcTokenStore.storeRefreshToken(refreshToken2, oAuth2Authentication2);

		OAuth2RefreshToken rt1, rt2;

		rt1 = customJdbcTokenStore.readRefreshToken(refreshToken1.getValue());
		rt2 = customJdbcTokenStore.readRefreshToken(refreshToken2.getValue());

		assertNotNull(rt1);
		assertNotNull(rt2);

		customJdbcTokenStore.removeRefreshTokenByUsername(USERNAME);

		rt1 = customJdbcTokenStore.readRefreshToken(refreshToken1.getValue());
		rt2 = customJdbcTokenStore.readRefreshToken(refreshToken2.getValue());

		assertNull(rt1);
		assertNull(rt2);

	}

	@Test
	void shouldDeleteByUsernameAndClientDetails() {
		CustomJdbcTokenStore customJdbcTokenStore = new CustomJdbcTokenStore(dataSource);

		customJdbcTokenStore.storeRefreshToken(refreshToken1, oAuth2Authentication1);
		customJdbcTokenStore.storeRefreshToken(refreshToken2, oAuth2Authentication2);

		OAuth2RefreshToken rt1, rt2;

		rt1 = customJdbcTokenStore.readRefreshToken(refreshToken1.getValue());
		rt2 = customJdbcTokenStore.readRefreshToken(refreshToken2.getValue());

		assertNotNull(rt1);
		assertNotNull(rt2);

		customJdbcTokenStore.removeRefreshTokenByUsernameAndClientId(USERNAME, CLIENT_ID_1);

		rt1 = customJdbcTokenStore.readRefreshToken(refreshToken1.getValue());
		rt2 = customJdbcTokenStore.readRefreshToken(refreshToken2.getValue());

		assertNull(rt1);
		assertNotNull(rt2);

		// cleanUp
		customJdbcTokenStore.removeRefreshTokenByUsername(USERNAME);
	}

}
