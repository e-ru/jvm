package eu.rudisch.authorizationserver.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import eu.rudisch.authorizationserver.service.JwtExtractor;

public class RefreshTokenFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LogManager.getLogger(RefreshTokenFilter.class);

	private JwtExtractor jwtExtractor;

	public RefreshTokenFilter(JwtExtractor jwtExtractor) {
		this.jwtExtractor = jwtExtractor;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		try {
			jwtExtractor.extract(request);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new InsufficientAuthenticationException("The user is not authenticated.");
		}
		chain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		return !path.startsWith(WebSecurityConfiguration.TOKENS_PATTERN);
	}
}
