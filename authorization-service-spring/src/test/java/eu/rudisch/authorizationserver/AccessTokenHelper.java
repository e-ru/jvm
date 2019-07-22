package eu.rudisch.authorizationserver;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AccessTokenHelper {
	public static String obtainAccessTokenByAuthorizationCode(MockMvc mockMvc, String username, String password,
			String scope) throws Exception {
		MockHttpSession session = new MockHttpSession();

		String authorizeUrl = "http://localhost/oauth/authorize";
		String responseType = "code";
		String clientId = "auth_server";
		String redirectUrl = "http://localhost:8889/redirect.html";
		String state = "state";

		String url = authorizeUrl
				+ "?response_type=" + responseType
				+ "&scope=" + scope
				+ "&client_id=" + clientId
				+ "&redirect_uri=" + redirectUrl
				+ "&state=" + state;
		String loginUrl = "http://localhost/login";

		MvcResult mvcResult = mockMvc.perform(get(url)
				.session(session))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl(loginUrl))
				.andReturn();

		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("username", username);
		params.add("password", password);

		mvcResult = mockMvc.perform(post(loginUrl)
				.params(params)
				.session(session))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl(url.replace(" ", "%20")))
				.andReturn();

		String confirmAccessPath = "/oauth/confirm_access";

		mvcResult = mockMvc.perform(get(url)
				.session(session))
				.andExpect(status().isOk())
				.andExpect(forwardedUrl(confirmAccessPath))
				.andReturn();

		params.clear();
		params.add("user_oauth_approval", "true");
		for (String s : scope.split(" "))
			params.add("scope." + s, "true");
		params.add("authorize", "Authorize");

		mvcResult = mockMvc.perform(post(authorizeUrl)
				.session(session)
				.params(params))
				.andExpect(status().isFound())
//				.andExpect(redirectedUrl(redirectUrl)) TODO: add regex to match code pattern (Redirected URL expected:<http://localhost:8889/redirect.html> but was:<http://localhost:8889/redirect.html?code=xxxxxx&state=state>)
				.andReturn();

		String resultString = mvcResult.getResponse().getRedirectedUrl();
		assertNotNull(resultString);
		assertTrue(resultString.contains("code"));
		assertTrue(resultString.contains("state"));

		resultString = resultString.substring(resultString.indexOf("=") + 1);
		String code = resultString.substring(0, resultString.indexOf("&"));
		String tokenPath = "http://localhost/oauth/token";

		params.clear();
		params.add("grant_type", "authorization_code");
		params.add("code", code);
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUrl);
		params.add("scope", scope);

		mvcResult = mockMvc.perform(post(tokenPath)
				.params(params)
				.session(session)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andReturn();

		resultString = mvcResult.getResponse().getContentAsString();
		JacksonJsonParser jsonParser = new JacksonJsonParser();
		String accessToken = jsonParser.parseMap(resultString).get("access_token").toString();
		assertNotNull(accessToken);

		return accessToken;
	}
}
