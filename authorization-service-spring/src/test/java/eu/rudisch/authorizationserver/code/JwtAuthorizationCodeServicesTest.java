package eu.rudisch.authorizationserver.code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.rudisch.authorizationserver.AuthorizationServerApplication;
import eu.rudisch.authorizationserver.model.AuthUserDetail;
import eu.rudisch.authorizationserver.model.Permission;
import eu.rudisch.authorizationserver.model.Role;
import eu.rudisch.authorizationserver.model.User;
import eu.rudisch.authorizationserver.service.JwtExtractorImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AuthorizationServerApplication.class)
class JwtAuthorizationCodeServicesTest {

	static final String USERNAME = "test_user";
	static final String ROLE = "test_role";
	static final String PERMISSION = "test_permission";
	static final String CLIENT_ID = "test_clientId";
	static final Set<String> SCOPE = Set.of("test_scope");
	static final Set<String> RESOURCE_IDS = Set.of("test_resource");
	static final String REDIRECT_URI = "test_redirectUri";

	static final String CODE = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0X2NsaWVudElkIiwicmRyIjoidGVzdF9yZWRpcmVjdFVyaSIsInVzciI6InRlc3RfdXNlciIsInBtcyI6WyJ0ZXN0X3Njb3BlIl0sImV4cCI6MzMxMjA0Nzc4NTI0MTcsImp0aSI6IjQ0NDdlNmE3LTI1NGItNDI0Yi05ZTlkLWViNThkODBjZmNmZiJ9.SEdC8UORHIqUjJ5ZSsK1tDL1KihNNR1lSlEkR3JSYseYa5udShEd2YPfJ_3nsR6qKioVDTM3ODapkC8TWPQl_dWLycS-zFKLXuIemHkrDAnzKlAVwnlTLBuIqHuGmKQcGyFfm4w266cXjmS1X2osCCIt_CHZ8yhwVxuxqzChBnfSTrbjGcgDwLnzKMK3cmEHm8l-ghAOSLrx-_0dWweObQD5XL__PGaKwJzVxVPUZIjaM2kM2ge4B-abdw26qJ0YJ_3bsS5RhAKYrDvbgyLKr-smTBUVKL7dsQxr4Jk-Rf122eRI6be5vEVs9peTMToDuUGJJnJKZ3LnrX9deBFoXw";

	@TestConfiguration
	static class JwtAuthorizationCodeServicesTestContextConfiguration {
		@Bean
		public JwtAuthorizationCodeServices jwtAuthorizationCodeServices() {
			return Mockito.mock(JwtAuthorizationCodeServices.class);
		}
	}

	@Autowired
	private JwtAuthorizationCodeServices jwtAuthorizationCodeServices;

	@InjectMocks
	private JwtExtractorImpl jwtExtractor;
	@MockBean
	private JwtAuthorizationCodeServicesHelperServiceImpl helperService;

	private JsonParser objectMapper = JsonParserFactory.create();

	private static OAuth2Authentication oAuth2Authentication;

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
	}

	@Test
	void createAuthorizationCode() throws InvalidKeySpecException, NoSuchAlgorithmException {
		String code = jwtAuthorizationCodeServices.createAuthorizationCode(oAuth2Authentication);

		Jwt jwtCode = JwtHelper.decode(code);
		String claimsStr = jwtCode.getClaims();
		Map<String, Object> claims = objectMapper.parseMap(claimsStr);

		assertEquals(claims.get(JwtAuthorizationCodeServices.SUBJECT), CLIENT_ID);
		assertEquals(claims.get(JwtAuthorizationCodeServices.CLAIM_REDIRECT_URL), REDIRECT_URI);
		assertEquals(claims.get(JwtAuthorizationCodeServices.CLAIM_USER), USERNAME);
		assertEquals(jwtAuthorizationCodeServices.scope(claims.get(JwtAuthorizationCodeServices.CLAIM_PERMISSIONS)),
				SCOPE);
	}

	@Test
	public void consumeAuthorizationCode() {
		Jwt jwtCode = JwtHelper.decode(CODE);
		String claimsStr = jwtCode.getClaims();
		Map<String, Object> claims = objectMapper.parseMap(claimsStr);

		String clientId = (String) claims.get(JwtAuthorizationCodeServices.SUBJECT);
		Set<String> scope = jwtAuthorizationCodeServices
				.scope(claims.get(JwtAuthorizationCodeServices.CLAIM_PERMISSIONS));
		String redirectUrl = (String) claims.get(JwtAuthorizationCodeServices.CLAIM_REDIRECT_URL);
		String user = (String) claims.get(JwtAuthorizationCodeServices.CLAIM_USER);

		Mockito.when(helperService.oAuth2Authentication(clientId, scope, redirectUrl, user))
				.thenReturn(oAuth2Authentication);

		OAuth2Authentication oAuth2Authentication = jwtAuthorizationCodeServices.consumeAuthorizationCode(CODE);

		Set<String> authorities = oAuth2Authentication.getAuthorities().stream()
				.map(ga -> ga.getAuthority())
				.collect(Collectors.toSet());
		assertTrue(authorities.contains(ROLE));
		assertTrue(authorities.contains(PERMISSION));
		assertEquals(oAuth2Authentication.getOAuth2Request().getClientId(), CLIENT_ID);
		assertEquals(oAuth2Authentication.getOAuth2Request().getRedirectUri(), REDIRECT_URI);
		assertEquals(oAuth2Authentication.getOAuth2Request().getResourceIds(), RESOURCE_IDS);
		assertEquals(oAuth2Authentication.getUserAuthentication().getName(), USERNAME);
	}

}
