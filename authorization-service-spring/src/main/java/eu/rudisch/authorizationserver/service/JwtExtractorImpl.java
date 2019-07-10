package eu.rudisch.authorizationserver.service;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.stereotype.Service;

@Service
public class JwtExtractorImpl implements JwtExtractor {

	@Autowired
	private KeyPair keyPair;

	@Override
	public Jwt extract(HttpServletRequest request) {
		BearerTokenExtractor bearerTokenExtractor = new BearerTokenExtractor();
		Authentication authentication = bearerTokenExtractor.extract(request);

		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		String token = (String) authentication.getPrincipal();

		return JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
	}

}
