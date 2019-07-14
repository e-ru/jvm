package eu.rudisch.users.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import eu.rudisch.users.Main;

@Provider
@JwtOAuth2
@Priority(Priorities.AUTHENTICATION)
public class JwtOAuth2Filter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		System.out.println("drin");

		try {
			Class<?> resourceClass = resourceInfo.getResourceClass();
			Method resourceMethod = resourceInfo.getResourceMethod();
			JwtOAuth2 classAnnotation = resourceClass.getAnnotation(JwtOAuth2.class);
			JwtOAuth2 methodAnnotation = resourceMethod.getAnnotation(JwtOAuth2.class);
			String scope = null;
			if (classAnnotation != null)
				scope = classAnnotation.scope();
			if (methodAnnotation != null)
				scope = methodAnnotation.scope();

			String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
			String token = authorizationHeader.substring("Bearer".length()).trim();

			String publicKeyContent = Main.TOKEN_KEY
					.replaceAll("\\n", "")
					.replace("-----BEGIN PUBLIC KEY-----", "")
					.replace("-----END PUBLIC KEY-----", "");
			KeyFactory kf = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
			RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

			Algorithm algorithm = Algorithm.RSA256(pubKey, null);
			JWTVerifier verifier = JWT.require(algorithm)
					.build(); // Reusable verifier instance
			DecodedJWT jwt = verifier.verify(token);

			Claim scopes = jwt.getClaim("scope");
			List<String> sps = scopes.asList(String.class);

			if (!sps.contains(scope))
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

}
