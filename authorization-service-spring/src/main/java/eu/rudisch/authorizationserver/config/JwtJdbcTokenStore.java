package eu.rudisch.authorizationserver.config;

import java.util.Collection;
import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

public class JwtJdbcTokenStore extends JdbcTokenStore {

	private CustomJwtAccessTokenConverter jwtTokenEnhancer;

	public JwtJdbcTokenStore(CustomJwtAccessTokenConverter jwtTokenEnhancer, DataSource dataSource) {
		super(dataSource);
		this.jwtTokenEnhancer = jwtTokenEnhancer;
	}

	@Override
	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		return readAuthentication(token.getValue());
	}

	@Override
	public OAuth2Authentication readAuthentication(String token) {
		return jwtTokenEnhancer.extractAuthentication(jwtTokenEnhancer.decode(token));
	}

	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
	}

	@Override
	public OAuth2AccessToken readAccessToken(String tokenValue) {
		OAuth2AccessToken accessToken = convertAccessToken(tokenValue);
		if (jwtTokenEnhancer.isRefreshToken(accessToken)) {
			throw new InvalidTokenException("Encoded token is a refresh token");
		}
		return accessToken;
	}

	private OAuth2AccessToken convertAccessToken(String tokenValue) {
		return jwtTokenEnhancer.extractAccessToken(tokenValue, jwtTokenEnhancer.decode(tokenValue));
	}

	@Override
	public void removeAccessToken(OAuth2AccessToken token) {
	}

	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
		// gh-807 Approvals (if any) should only be removed when Refresh Tokens are removed (or expired)
	}

	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		// We don't want to accidentally issue a token, and we have no way to reconstruct the refresh token
		return null;
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
		return Collections.emptySet();
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
		return Collections.emptySet();
	}
}
