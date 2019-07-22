package eu.rudisch.authorizationserver.token;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.rudisch.authorizationserver.AuthorizationServerApplication;
import eu.rudisch.authorizationserver.code.CodeAuthentication;
import eu.rudisch.authorizationserver.model.AuthUserDetail;
import eu.rudisch.authorizationserver.model.Permission;
import eu.rudisch.authorizationserver.model.Role;
import eu.rudisch.authorizationserver.model.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AuthorizationServerApplication.class)
class CustomTokenStoreTest {

	static final String USERNAME = "test_user";
	static final String ROLE = "test_role";
	static final String PERMISSION = "test_permission";
	static final String CLIENT_ID = "test_clientId";
	static final Set<String> SCOPE = Set.of("test_scope");
	static final Set<String> RESOURCE_IDS = Set.of("test_resource");
	static final String REDIRECT_URI = "test_redirectUri";

	@MockBean
	private JwtTokenStore jwtTokenStore;
	@MockBean
	private CustomJdbcTokenStore customJdbcTokenStore;

	private static OAuth2Authentication oAuth2Authentication;

	private static OAuth2RefreshToken refreshToken;

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

		oAuth2Authentication = new OAuth2Authentication(
				new OAuth2Request(null, CLIENT_ID, null, true, SCOPE, RESOURCE_IDS, REDIRECT_URI,
						null, null),
				new CodeAuthentication(authUserDetail));

		refreshToken = new DefaultExpiringOAuth2RefreshToken(UUID.randomUUID().toString(),
				new Date(System.currentTimeMillis() + (10000L * 1000L)));
	}

	@Test
	void shouldCall_storeRefreshToken() {
		CustomTokenStore customTokenStore = new CustomTokenStore(jwtTokenStore, customJdbcTokenStore);

		doNothing().when(customJdbcTokenStore).storeRefreshToken(any(OAuth2RefreshToken.class),
				any(OAuth2Authentication.class));

		customTokenStore.storeRefreshToken(refreshToken, oAuth2Authentication);

		verify(customJdbcTokenStore).storeRefreshToken(any(OAuth2RefreshToken.class), any(OAuth2Authentication.class));
	}

	@Test
	void shouldCall_removeRefreshTokenByUsername() {
		CustomTokenStore customTokenStore = new CustomTokenStore(jwtTokenStore, customJdbcTokenStore);

		doNothing().when(customJdbcTokenStore).removeRefreshTokenByUsername(any(String.class));

		customTokenStore.removeRefreshToken(USERNAME, null);

		verify(customJdbcTokenStore, never()).removeRefreshTokenByUsernameAndClientId(any(String.class),
				any(String.class));
		verify(customJdbcTokenStore).removeRefreshTokenByUsername(any(String.class));
	}

	@Test
	void shouldCall_removeRefreshTokenByUsernameAndClientId() {
		CustomTokenStore customTokenStore = new CustomTokenStore(jwtTokenStore, customJdbcTokenStore);

		doNothing().when(customJdbcTokenStore).removeRefreshTokenByUsernameAndClientId(any(String.class),
				any(String.class));

		customTokenStore.removeRefreshToken(USERNAME, CLIENT_ID);

		verify(customJdbcTokenStore, never()).removeRefreshTokenByUsername(any(String.class));
		verify(customJdbcTokenStore).removeRefreshTokenByUsernameAndClientId(any(String.class), any(String.class));
	}

}
