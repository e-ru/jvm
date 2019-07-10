package eu.rudisch.authorizationserver.service;

import org.springframework.security.jwt.Jwt;

public interface IssuerDetailService {

	void extractDetails(Jwt jwt);

	String getIssuerUsername();

	boolean isOAuthAdmin();
}
