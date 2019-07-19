package eu.rudisch.authorizationserver.token;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import eu.rudisch.authorizationserver.config.CustomProperties;

public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {

	@Autowired
	private CustomProperties customProperties;
	@Autowired
	private JdbcClientDetailsService jdbcClientDetailsService;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken.getValue());

		result.setScope(accessToken.getScope());
		Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
		if (!info.containsKey(TOKEN_ID))
			info.put(TOKEN_ID, result.getValue());
		result.setExpiration(accessToken.getExpiration());
		result.setTokenType(accessToken.getTokenType());
		result.setAdditionalInformation(info);
		result.setValue(encode(result, authentication));
		result.setRefreshToken(customProperties.isReuseRefreshtoken()
				? accessToken.getRefreshToken()
				: createRefreshToken(authentication));
		return result;
	}

	private OAuth2RefreshToken createRefreshToken(OAuth2Authentication authentication) {
		ClientDetails clientDetails = jdbcClientDetailsService
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
