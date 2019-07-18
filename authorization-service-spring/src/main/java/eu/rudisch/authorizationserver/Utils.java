package eu.rudisch.authorizationserver;

import java.security.KeyPair;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

public final class Utils {
	private Utils() {
	}

	public static KeyPair genKeyPair(String path, String password, String alias) {
		final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(path),
				password.toCharArray());
		return keyStoreKeyFactory.getKeyPair(alias);
	}
}
