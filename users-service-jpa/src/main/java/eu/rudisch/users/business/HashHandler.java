package eu.rudisch.users.business;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;

public class HashHandler {

	public enum HashType {
		SHA256
	}

	public String generateHash(String plain, HashType hashType) {
		if (hashType == HashType.SHA256)
			return generateSha256(plain);
		return plain;
	}

	String generateSha256(String plain) {
		return Hashing.sha256().hashString(plain, StandardCharsets.UTF_8).toString();
	}

	public boolean validate(String plain, String hash) {
		return hash.equals(generateSha256(plain));
	}

}
