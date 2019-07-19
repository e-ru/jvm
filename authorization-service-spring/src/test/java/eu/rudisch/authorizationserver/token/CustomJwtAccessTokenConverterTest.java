package eu.rudisch.authorizationserver.token;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.rudisch.authorizationserver.AuthorizationServerApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AuthorizationServerApplication.class)
class CustomJwtAccessTokenConverterTest {

	static final String ACCESS_TOKEN_VALUE = "test_access_token_value";

	@TestConfiguration
	static class CustomJwtAccessTokenConverterContextConfiguration {
		@Bean
		public CustomJwtAccessTokenConverter customJwtAccessTokenConverter() {
			return Mockito.mock(CustomJwtAccessTokenConverter.class);
		}
	}

	@Autowired
	private CustomJwtAccessTokenConverter customJwtAccessTokenConverter;

	@Test
	void test_enhance() {
		OAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(ACCESS_TOKEN_VALUE);

//		fail("Not yet implemented");
	}

}
