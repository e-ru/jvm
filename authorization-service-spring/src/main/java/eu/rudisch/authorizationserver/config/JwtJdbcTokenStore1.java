package eu.rudisch.authorizationserver.config;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.Assert;

public class JwtJdbcTokenStore1 implements TokenStore {

	private static final Log LOG = LogFactory.getLog(JwtJdbcTokenStore1.class);

	private static final String DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT = "insert into oauth_refresh_token (token_id, token, authentication) values (?, ?, ?)";

	private static final String DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT = "delete from oauth_refresh_token where token_id = ?";

	private static final String DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT = "select token_id, token from oauth_refresh_token where token_id = ?";

	private static final String DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "select token_id, authentication from oauth_refresh_token where token_id = ?";

	private String insertRefreshTokenSql = DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT;

	private String selectRefreshTokenSql = DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT;

	private String selectRefreshTokenAuthenticationSql = DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT;

	private String deleteRefreshTokenSql = DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT;

	private final JdbcTemplate jdbcTemplate;

	private CustomJwtAccessTokenConverter jwtTokenEnhancer;

	public JwtJdbcTokenStore1(CustomJwtAccessTokenConverter jwtTokenEnhancer, DataSource dataSource) {
		Assert.notNull(dataSource, "DataSource required");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jwtTokenEnhancer = jwtTokenEnhancer;
	}

	protected String extractTokenKey(String value) {
		if (value == null) {
			return null;
		}
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		}

		try {
			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
			return String.format("%032x", new BigInteger(1, bytes));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		}
	}

	protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
		return SerializationUtils.serialize(authentication);
	}

	protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
		return SerializationUtils.deserialize(authentication);
	}

	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
		jdbcTemplate.update(insertRefreshTokenSql, new Object[] { extractTokenKey(refreshToken.getValue()),
				new SqlLobValue(serializeRefreshToken(refreshToken)),
				new SqlLobValue(serializeAuthentication(authentication)) },
				new int[] { Types.VARCHAR, Types.BLOB,
						Types.BLOB });
	}

	public OAuth2RefreshToken readRefreshToken(String token) {
		OAuth2RefreshToken refreshToken = null;

		try {
			refreshToken = jdbcTemplate.queryForObject(selectRefreshTokenSql, new RowMapper<OAuth2RefreshToken>() {
				public OAuth2RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
					return deserializeRefreshToken(rs.getBytes(2));
				}
			}, extractTokenKey(token));
		} catch (EmptyResultDataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find refresh token for token " + token);
			}
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to deserialize refresh token for token " + token, e);
			removeRefreshToken(token);
		}

		return refreshToken;
	}

	public void removeRefreshToken(OAuth2RefreshToken token) {
		removeRefreshToken(token.getValue());
	}

	public void removeRefreshToken(String token) {
		jdbcTemplate.update(deleteRefreshTokenSql, extractTokenKey(token));
	}

	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
		return readAuthenticationForRefreshToken(token.getValue());
	}

	public OAuth2Authentication readAuthenticationForRefreshToken(String value) {
		OAuth2Authentication authentication = null;

		try {
			authentication = jdbcTemplate.queryForObject(selectRefreshTokenAuthenticationSql,
					new RowMapper<OAuth2Authentication>() {
						public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
							return deserializeAuthentication(rs.getBytes(2));
						}
					}, extractTokenKey(value));
		} catch (EmptyResultDataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find access token for token " + value);
			}
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to deserialize access token for " + value, e);
			removeRefreshToken(value);
		}

		return authentication;
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
