package eu.rudisch.authorizationserver.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.jwt.Jwt;

public interface JwtExtractor {
	Jwt extract(HttpServletRequest request);
}
