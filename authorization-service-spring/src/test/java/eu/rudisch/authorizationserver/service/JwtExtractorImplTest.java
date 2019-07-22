package eu.rudisch.authorizationserver.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.KeyPair;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.rudisch.authorizationserver.AuthorizationServerApplication;
import eu.rudisch.authorizationserver.Utils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AuthorizationServerApplication.class)
class JwtExtractorImplTest {

	private static final String TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsidGVzdF9yZXNvdXJjZSJdLCJ1c2VyX25hbWUiOiJ0ZXN0X3VzZXIiLCJzY29wZSI6WyJ0ZXN0X3Njb3BlIl0sImV4cCI6MTU2MzgwMTc0NywiYXV0aG9yaXRpZXMiOlsidGVzdF9yb2xlIiwidGVzdF9wZXJtaXNzaW9uIl0sImp0aSI6InRlc3RfYWNjZXNzX3Rva2VuX3ZhbHVlIiwiY2xpZW50X2lkIjoidGVzdF9jbGllbnRJZCJ9.Kxmw8Jjy17RAnZMZcsC-bUIZKCP4hvIhZIR_fkZEdw8GKtIIXKf3PQqU5apxlII_q2ZD_e4BusUpx3Rj1HAFvBsn7A1jJqC1lwUn_fTcBqAhOfQteUUkRmcBe6eNOD2WUtmDjIvP2sU_jzgELzwRP-I__IfMqqCNTehtPrSLL2TMXvH5hSx8F5W0ou-R-bBxeCMofrX4tg7BhlEgTYxCkjmhPD-yeZqpsuKfmlfMuNX2qkaBYSoUcuTK-XqHbRHWBi_tTYPPZEI_JoRdE_BUcbLixcZAs-0XHMje2BUv5PlUnGLR522SjvGayCEIGWH3tzMeszGfYhQrea6N9g-QxQ";

	@TestConfiguration
	static class JwtExtractorImplContextConfiguration {
		@Bean
		public KeyPair keyPair() {
			return Utils.genKeyPair("mytest.jks", "mypass", "mytest");
		}

		@Bean
		public JwtExtractorImpl JwtExtractorImpl() {
			return Mockito.mock(JwtExtractorImpl.class);
		}
	}

	@InjectMocks
	KeyPair keyPair;

	@Autowired
	JwtExtractorImpl jwtExtractorImpl;

	@Test
	void shouldExtractFromString_withoutException() {
		jwtExtractorImpl.extract(TOKEN);
	}

	@Test
	void shouldExtractFromString_withException() {
		assertThrows(RuntimeException.class, () -> jwtExtractorImpl.extract("fsdfsdf.fdsfsdfsdf.sfdsdfsdfsd"));
	}

}
