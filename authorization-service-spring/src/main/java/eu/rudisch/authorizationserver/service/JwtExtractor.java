package eu.rudisch.authorizationserver.service;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.jwt.Jwt;

public interface JwtExtractor {
	PrivateKey privateKey();

	RSAPublicKey publicKey();

	Jwt extract(HttpServletRequest request);

	Jwt extract(String token);

	String encode(String tokenContent);

}
