package eu.rudisch.authorizationserver.config;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

public class CustomTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		final Map<String, Object> additionalInfo = new HashMap<>();
		additionalInfo.put("organization", authentication.getName() + randomAlphabetic(4));
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
//		((DefaultOAuth2AccessToken) accessToken).setRefreshToken(createRefreshToken(authentication));
		return accessToken;
	}

	private OAuth2RefreshToken createRefreshToken(OAuth2Authentication authentication) {
//		if (!isSupportRefreshToken(authentication.getOAuth2Request())) {
//			return null;
//		}
		int validitySeconds = getRefreshTokenValiditySeconds(authentication.getOAuth2Request());
		String value = UUID.randomUUID().toString();
		if (validitySeconds > 0) {
			return new DefaultExpiringOAuth2RefreshToken(value, new Date(System.currentTimeMillis()
					+ (validitySeconds * 1000L)));
		}
		return new DefaultOAuth2RefreshToken(value);
	}

	private int refreshTokenValiditySeconds = 60 * 60 * 24 * 30; // default 30 days.

	protected int getRefreshTokenValiditySeconds(OAuth2Request clientAuth) {
//		if (clientDetailsService != null) {
//			ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
//			Integer validity = client.getRefreshTokenValiditySeconds();
//			if (validity != null) {
//				return validity;
//			}
//		}
		return refreshTokenValiditySeconds;
	}
}
