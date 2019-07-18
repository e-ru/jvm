package eu.rudisch.authorizationserver.code;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.util.Assert;

import eu.rudisch.authorizationserver.model.User;

public class JwtAuthorizationCodeServices implements AuthorizationCodeServices {

	private static final int DEFAULT_AUTH_CODE_DURATION = 30;

	private static final String SUBJECT = "sub";
	private static final String CLAIM_REDIRECT_URL = "rdr";
	private static final String CLAIM_USER = "usr";
	private static final String CLAIM_ID = "jti";
	private static final String CLAIM_EXP = "exp";
	private static final String CLAIM_PERMISSIONS = "pms";

	private JsonParser objectMapper = JsonParserFactory.create();

	private UserDetailsService userDetailsService;
	private JdbcClientDetailsService jdbcClientDetailsService;
	private Signer signer;
	private SignatureVerifier verifier;

//	public JwtAuthorizationCodeServices(UserDetailsService userDetailsService,
//			DataSource dataSource, KeyPair keyPair) {
//		this.userDetailsService = userDetailsService;
//		jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
//		setSignerAndVerifer(keyPair);
//	}

	public JwtAuthorizationCodeServices(UserDetailsService userDetailsService,
			JdbcClientDetailsService jdbcClientDetailsService, KeyPair keyPair) {
		this.userDetailsService = userDetailsService;
		this.jdbcClientDetailsService = jdbcClientDetailsService;
		setSignerAndVerifer(keyPair);
	}

	private void setSignerAndVerifer(KeyPair keyPair) {
		PrivateKey privateKey = keyPair.getPrivate();
		Assert.state(privateKey instanceof RSAPrivateKey, "KeyPair must be an RSA ");
		signer = new RsaSigner((RSAPrivateKey) privateKey);
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		verifier = new RsaVerifier(publicKey);
	}

	@Override
	public String createAuthorizationCode(OAuth2Authentication authentication) {
		String content;
		try {
			long now = System.currentTimeMillis();
			Duration expire = Duration.ofSeconds(DEFAULT_AUTH_CODE_DURATION);

			Map<String, Object> codeMap = new HashMap<>();
			User user = (User) authentication.getUserAuthentication().getPrincipal();
			OAuth2Request auth2Request = authentication.getOAuth2Request();

			codeMap.put(SUBJECT, auth2Request.getClientId());
			codeMap.put(CLAIM_REDIRECT_URL, auth2Request.getRedirectUri());
			codeMap.put(CLAIM_USER, user.getUsername());
			codeMap.put(CLAIM_EXP, new Date(now + expire.toMillis()));
			codeMap.put(CLAIM_ID, UUID.randomUUID().toString());
			codeMap.put(CLAIM_PERMISSIONS, auth2Request.getScope());
			content = objectMapper.formatMap(codeMap);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot convert authentication to JSON", e);
		}

		String code = JwtHelper.encode(content, signer).getEncoded();
		return code;
	}

	@Override
	public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
		try {
			Jwt jwt = JwtHelper.decodeAndVerify(code, verifier);
			String claimsStr = jwt.getClaims();
			Map<String, Object> claims = objectMapper.parseMap(claimsStr);

			long now = System.currentTimeMillis();
			long exp = (Long) claims.get(CLAIM_EXP);
			if (exp - now < 0)
				throw new OAuth2Exception("Authentication code expired");

			String user = (String) claims.get(CLAIM_USER);
			String clientId = (String) claims.get(SUBJECT);
			String redirectUrl = (String) claims.get(CLAIM_REDIRECT_URL);

			@SuppressWarnings("unchecked")
			Set<String> scope = ((List<String>) claims.get(CLAIM_PERMISSIONS)).stream()
					.collect(Collectors.toSet());

			UserDetails userDetails = userDetailsService.loadUserByUsername(user);
			Authentication authentication = new CodeAuthentication(userDetails);

			ClientDetails clientDetails = jdbcClientDetailsService.loadClientByClientId(clientId);
			Set<String> resourceIds = clientDetails.getResourceIds();

			OAuth2Request auth2Request = new OAuth2Request(null, clientId, null, false, scope, resourceIds, redirectUrl,
					null, null);

			return new OAuth2Authentication(auth2Request, authentication);
		} catch (Exception e) {
			throw new InvalidTokenException("Cannot convert code to OAuth2Authentication", e);
		}
	}
}
