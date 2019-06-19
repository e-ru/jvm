package eu.rudisch.authorizationserver.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import eu.rudisch.authorizationserver.model.User;

public class CustomJdbcTokenStore extends JdbcTokenStore {

	private static final Log LOG = LogFactory.getLog(CustomJdbcTokenStore.class);

	private static final String REFRESH_TOKEN_INSERT_STATEMENT = "insert into oauth_refresh_token (token_id, client_id, username, token, authentication) values (?, ?, ?, ?, ?)";
	private static final String REFRESH_TOKEN_SELECT_BY_USERNAME_STATEMENT = "select token_id, token from oauth_refresh_token where username = ?";
	private static final String REFRESH_TOKEN_INSERT_STATEMENT_BY_USERNAME_AND_CLIENT_ID = "select token_id, token from oauth_refresh_token where username = ? and client_id = ?";

	private final JdbcTemplate jdbcTemplate;

	public CustomJdbcTokenStore(DataSource dataSource) {
		super(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
		jdbcTemplate.update(REFRESH_TOKEN_INSERT_STATEMENT,
				new Object[] { extractTokenKey(refreshToken.getValue()),
						authentication.getOAuth2Request().getClientId(),
						((User) authentication.getPrincipal()).getUsername(),
						new SqlLobValue(serializeRefreshToken(refreshToken)),
						new SqlLobValue(serializeAuthentication(authentication)) },
				new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.BLOB });
	}

	public void removeRefreshTokenByUsername(String username) {
		List<OAuth2RefreshToken> refreshTokens = Collections.emptyList();
		try {
			refreshTokens = jdbcTemplate.query(REFRESH_TOKEN_SELECT_BY_USERNAME_STATEMENT,
					new RowMapper<OAuth2RefreshToken>() {
						public OAuth2RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
							return deserializeRefreshToken(rs.getBytes(2));
						}
					}, username);
		} catch (EmptyResultDataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find refresh token for username " + username);
			}
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to deserialize refresh token for username " + username, e);
		}
		refreshTokens.forEach(refreshToken -> super.removeRefreshToken(refreshToken));
	}

	public void removeRefreshTokenByUsernameAndClientId(String username, String clientId) {
		List<OAuth2RefreshToken> refreshTokens = Collections.emptyList();
		try {
			refreshTokens = jdbcTemplate.query(REFRESH_TOKEN_INSERT_STATEMENT_BY_USERNAME_AND_CLIENT_ID,
					new RowMapper<OAuth2RefreshToken>() {
						public OAuth2RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
							return deserializeRefreshToken(rs.getBytes(2));
						}
					}, username, clientId);
		} catch (EmptyResultDataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find refresh token for username " + username + " and clientId " + clientId);
			}
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to deserialize refresh token for username " + username + " and clientId " + clientId, e);
		}
		refreshTokens.forEach(refreshToken -> super.removeRefreshToken(refreshToken));
	}

}
