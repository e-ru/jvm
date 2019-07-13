package eu.rudisch.users;

import eu.rudisch.users.security.RestTokenKeyFetcher;

public class Main {

	public static final String TOKEN_KEY;

	static {
		RestTokenKeyFetcher restTokenKeyFetcher = new RestTokenKeyFetcher();
		TOKEN_KEY = restTokenKeyFetcher.getTokenKey();
	}

	public static void main(String[] args) {
		new JettyRunner().startServer();
	}

}
