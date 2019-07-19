package eu.rudisch.authorizationserver.service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class JwtExtractorImpl implements JwtExtractor {

	@Autowired
	private KeyPair keyPair;

	PrivateKey privateKey() {
		return keyPair.getPrivate();
	}

	RSAPublicKey publicKey() {
		return (RSAPublicKey) keyPair.getPublic();
	}

	Signer signer(PrivateKey privateKey) {
		Assert.state(privateKey instanceof RSAPrivateKey, "KeyPair must be an RSA ");
		return new RsaSigner((RSAPrivateKey) privateKey());
	}

	String token(HttpServletRequest request) {
		BearerTokenExtractor bearerTokenExtractor = new BearerTokenExtractor();
		Authentication authentication = bearerTokenExtractor.extract(request);

		return (String) authentication.getPrincipal();
	}

	@Override
	public Jwt extract(HttpServletRequest request) {
		return extract(token(request));
	}

	@Override
	public Jwt extract(String token) {
		return JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey()));
	}

	@Override
	public String encode(String tokenContent) {
		return JwtHelper.encode(tokenContent, signer(privateKey())).getEncoded();
	}

}
