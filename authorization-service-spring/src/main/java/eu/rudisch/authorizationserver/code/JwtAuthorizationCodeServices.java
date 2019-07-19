package eu.rudisch.authorizationserver.code;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;

import eu.rudisch.authorizationserver.model.User;
import eu.rudisch.authorizationserver.service.JwtExtractor;

public class JwtAuthorizationCodeServices implements AuthorizationCodeServices {

	private static final int DEFAULT_AUTH_CODE_DURATION = 30;

	public static final String SUBJECT = "sub";
	public static final String CLAIM_REDIRECT_URL = "rdr";
	public static final String CLAIM_USER = "usr";
	public static final String CLAIM_ID = "jti";
	public static final String CLAIM_EXP = "exp";
	public static final String CLAIM_PERMISSIONS = "pms";

	@Autowired
	private JwtExtractor jwtExtractor;
	@Autowired
	private JwtAuthorizationCodeServicesHelperService helperService;

	private JsonParser objectMapper = JsonParserFactory.create();

	private long authCodeDuration = DEFAULT_AUTH_CODE_DURATION;

	Date expires() {
		return new Date(System.currentTimeMillis() + Duration.ofSeconds(authCodeDuration).toMillis());
	}

	boolean expired(Long exp) {
		return exp - System.currentTimeMillis() < 0;
	}

	@SuppressWarnings("unchecked")
	Set<String> scope(Object permissions) {
		return ((List<String>) permissions).stream()
				.collect(Collectors.toSet());
	}

	String encode(String content) {
		return jwtExtractor.encode(content);
	}

	Jwt extract(String code) {
		return jwtExtractor.extract(code);
	}

	public void setAuthCodeDuration(long authCodeDuration) {
		this.authCodeDuration = authCodeDuration;
	}

	@Override
	public String createAuthorizationCode(OAuth2Authentication authentication) {
		String content;
		try {
			Map<String, Object> codeMap = new HashMap<>();
			User user = (User) authentication.getUserAuthentication().getPrincipal();
			OAuth2Request auth2Request = authentication.getOAuth2Request();

			codeMap.put(SUBJECT, auth2Request.getClientId());
			codeMap.put(CLAIM_REDIRECT_URL, auth2Request.getRedirectUri());
			codeMap.put(CLAIM_USER, user.getUsername());
			codeMap.put(CLAIM_EXP, expires());
			codeMap.put(CLAIM_ID, UUID.randomUUID().toString());
			codeMap.put(CLAIM_PERMISSIONS, auth2Request.getScope());
			content = objectMapper.formatMap(codeMap);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot convert authentication to JSON", e);
		}
		return encode(content);
	}

	@Override
	public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
		try {
			Map<String, Object> claims = objectMapper.parseMap(extract(code).getClaims());

			if (expired((Long) claims.get(CLAIM_EXP)))
				throw new OAuth2Exception("Authentication code expired");

			String clientId = (String) claims.get(SUBJECT);
			Set<String> scope = scope(claims.get(CLAIM_PERMISSIONS));
			String redirectUrl = (String) claims.get(CLAIM_REDIRECT_URL);
			String user = (String) claims.get(CLAIM_USER);

			return helperService.oAuth2Authentication(clientId, scope, redirectUrl, user);
		} catch (Exception e) {
			throw new InvalidTokenException("Cannot convert code to OAuth2Authentication", e);
		}
	}
}
