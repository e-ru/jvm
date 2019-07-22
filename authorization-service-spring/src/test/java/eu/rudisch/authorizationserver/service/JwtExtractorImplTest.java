package eu.rudisch.authorizationserver.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
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
		public JwtExtractorImpl jwtExtractorImpl() {
			return Mockito.mock(JwtExtractorImpl.class);
		}
	}

	static class EnumerationImpl implements Enumeration<String> {
		ArrayList<String> list = new ArrayList<>();

		public EnumerationImpl() {
			list.add(OAuth2AccessToken.BEARER_TYPE + " " + TOKEN);
		}

		@Override
		public boolean hasMoreElements() {
			return list.size() > 0;
		}

		@Override
		public String nextElement() {
			return list.remove(0);
		}
	};

	@InjectMocks
	KeyPair keyPair;

	@Autowired
	JwtExtractorImpl jwtExtractorImpl;

	@Mock
	HttpServletRequest request;

	@Test
	void shouldExtractFromHttpServletRequest_withoutException() {
		Enumeration<String> headers = new EnumerationImpl();

		Mockito.when(request.getHeaders("Authorization")).thenReturn(headers);
		doNothing().when(request).setAttribute(any(String.class), any(String.class));

		jwtExtractorImpl.extract(request);
	}

	@Test
	void shouldExtractFromString_withoutException() {
		jwtExtractorImpl.extract(TOKEN);
	}

	@Test
	void shouldExtractFromString_withException() {
		assertThrows(RuntimeException.class, () -> jwtExtractorImpl.extract("fsdfsdf.fdsfsdfsdf.sfdsdfsdfsd"));
	}

	@Test
	void shouldEncodeContentString() {
		Map<String, Object> codeMap = new HashMap<>();
		codeMap.put("aud", Set.of("test_resource"));
		codeMap.put("user_name", "test_user");
		codeMap.put("scope", Set.of("test_scope"));
		codeMap.put("exp", new Date(1563801747000L));
		codeMap.put("authorities", Set.of("test_role", "test_permission"));
		codeMap.put("jti", "test_access_token_value");
		codeMap.put("client_id", "test_clientId");
		String content = JsonParserFactory.create().formatMap(codeMap);

		String token = jwtExtractorImpl.encode(content);

		assertNotNull(token);
	}

}
