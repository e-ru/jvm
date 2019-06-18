package eu.rudisch.authorizationserver.config;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {
//	private JsonParser objectMapper = JsonParserFactory.create();

	JdbcClientDetailsService jbcClientDetailsService;

	public CustomJwtAccessTokenConverter(DataSource dataSource) {
		jbcClientDetailsService = new JdbcClientDetailsService(dataSource);
	}

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken.getValue());
		result.setScope(accessToken.getScope());
		Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
		String tokenId = result.getValue();
		if (!info.containsKey(TOKEN_ID)) {
			info.put(TOKEN_ID, tokenId);
		} else {
			tokenId = (String) info.get(TOKEN_ID);
		}
		result.setAdditionalInformation(info);
		result.setValue(encode(result, authentication));
//		OAuth2RefreshToken refreshToken = result.getRefreshToken();
//		if (refreshToken != null) {
		OAuth2RefreshToken refreshToken = createRefreshToken(authentication);
//		jdbcTokenStore.storeRefreshToken(refreshToken, authentication);

//		DefaultOAuth2AccessToken encodedRefreshToken = new DefaultOAuth2AccessToken(accessToken);
//		encodedRefreshToken.setValue(refreshToken.getValue());
//		// Refresh tokens do not expire unless explicitly of the right type
//		encodedRefreshToken.setExpiration(null);
//		try {
//			Map<String, Object> claims = objectMapper
//					.parseMap(JwtHelper.decode(refreshToken.getValue()).getClaims());
//			if (claims.containsKey(TOKEN_ID)) {
//				encodedRefreshToken.setValue(claims.get(TOKEN_ID).toString());
//			}
//		} catch (IllegalArgumentException e) {
//		}
//		Map<String, Object> refreshTokenInfo = new LinkedHashMap<String, Object>(
//				accessToken.getAdditionalInformation());
//		refreshTokenInfo.put(TOKEN_ID, encodedRefreshToken.getValue());
//		refreshTokenInfo.put(ACCESS_TOKEN_ID, tokenId);
//		encodedRefreshToken.setAdditionalInformation(refreshTokenInfo);
//		DefaultOAuth2RefreshToken token = new DefaultOAuth2RefreshToken(
//				encode(encodedRefreshToken, authentication));
//		if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
//			Date expiration = ((ExpiringOAuth2RefreshToken) refreshToken).getExpiration();
//			encodedRefreshToken.setExpiration(expiration);
//			token = new DefaultExpiringOAuth2RefreshToken(encode(encodedRefreshToken, authentication), expiration);
//		}
		result.setRefreshToken(refreshToken);
//		}
		return result;
	}

	private OAuth2RefreshToken createRefreshToken(OAuth2Authentication authentication) {
//		if (!isSupportRefreshToken(authentication.getOAuth2Request())) {
//			return null;
//		}
		ClientDetails clientDetails = jbcClientDetailsService
				.loadClientByClientId(authentication.getOAuth2Request().getClientId());
		int validitySeconds = clientDetails.getRefreshTokenValiditySeconds();
		String value = UUID.randomUUID().toString();
		if (validitySeconds > 0) {
			return new DefaultExpiringOAuth2RefreshToken(value, new Date(System.currentTimeMillis()
					+ (validitySeconds * 1000L)));
		}
		return new DefaultOAuth2RefreshToken(value);
	}
}
