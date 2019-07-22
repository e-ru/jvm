package eu.rudisch.authorizationserver.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.jwt.JwtHelper;

class IssuerDetailServiceImplTest {

	static final String USERNAME = "test_user";
	static final String ROLE = "test_role";

	private static final String TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsidGVzdF9yZXNvdXJjZSJdLCJ1c2VyX25hbWUiOiJ0ZXN0X3VzZXIiLCJzY29wZSI6WyJ0ZXN0X3Njb3BlIl0sImV4cCI6MTU2MzgwMTc0NywiYXV0aG9yaXRpZXMiOlsidGVzdF9yb2xlIiwidGVzdF9wZXJtaXNzaW9uIl0sImp0aSI6InRlc3RfYWNjZXNzX3Rva2VuX3ZhbHVlIiwiY2xpZW50X2lkIjoidGVzdF9jbGllbnRJZCJ9.Kxmw8Jjy17RAnZMZcsC-bUIZKCP4hvIhZIR_fkZEdw8GKtIIXKf3PQqU5apxlII_q2ZD_e4BusUpx3Rj1HAFvBsn7A1jJqC1lwUn_fTcBqAhOfQteUUkRmcBe6eNOD2WUtmDjIvP2sU_jzgELzwRP-I__IfMqqCNTehtPrSLL2TMXvH5hSx8F5W0ou-R-bBxeCMofrX4tg7BhlEgTYxCkjmhPD-yeZqpsuKfmlfMuNX2qkaBYSoUcuTK-XqHbRHWBi_tTYPPZEI_JoRdE_BUcbLixcZAs-0XHMje2BUv5PlUnGLR522SjvGayCEIGWH3tzMeszGfYhQrea6N9g-QxQ";

	@Test
	void should_extractDetails() {
		IssuerDetailServiceImpl issuerDetailServiceImpl = new IssuerDetailServiceImpl();

		issuerDetailServiceImpl.extractDetails(JwtHelper.decode(TOKEN));

		assertEquals(USERNAME, issuerDetailServiceImpl.getIssuerUsername());
		assertTrue(issuerDetailServiceImpl.checkRole(ROLE));
		assertFalse(issuerDetailServiceImpl.checkRole("foo"));
	}

}
