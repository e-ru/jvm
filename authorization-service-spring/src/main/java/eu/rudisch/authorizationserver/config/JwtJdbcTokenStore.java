package eu.rudisch.authorizationserver.config;

import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

public class JwtJdbcTokenStore implements TokenStore {

	private JdbcTokenStore jdbcTokenStore;
	private JwtTokenStore jwtTokenStore;

	public JwtJdbcTokenStore(JwtAccessTokenConverter jwtTokenEnhancer, DataSource dataSource) {
		jdbcTokenStore = new JdbcTokenStore(dataSource);
		jwtTokenStore = new JwtTokenStore(jwtTokenEnhancer);
	}

	@Override
	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		return jwtTokenStore.readAuthentication(token);
	}

	@Override
	public OAuth2Authentication readAuthentication(String token) {
		return jwtTokenStore.readAuthentication(token);
	}

	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		jwtTokenStore.storeAccessToken(token, authentication);
	}

	@Override
	public OAuth2AccessToken readAccessToken(String tokenValue) {
		return jwtTokenStore.readAccessToken(tokenValue);
	}

	@Override
	public void removeAccessToken(OAuth2AccessToken token) {
		jwtTokenStore.removeAccessToken(token);
	}

	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
		jdbcTokenStore.storeRefreshToken(refreshToken, authentication);
	}

	@Override
	public OAuth2RefreshToken readRefreshToken(String tokenValue) {
		return jdbcTokenStore.readRefreshToken(tokenValue);
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
		return jdbcTokenStore.readAuthenticationForRefreshToken(token);
	}

	@Override
	public void removeRefreshToken(OAuth2RefreshToken token) {
		jdbcTokenStore.removeRefreshToken(token);
	}

	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
		jwtTokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
	}

	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		return jwtTokenStore.getAccessToken(authentication);
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
		return jwtTokenStore.findTokensByClientIdAndUserName(clientId, userName);
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
		return jwtTokenStore.findTokensByClientId(clientId);
	}

}
