package eu.rudisch.authorizationserver;

import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest // (classes = AuthorizationServerApplication.class)
@ActiveProfiles("mvc")
public class OAuthMvcTest {

	public class SessionTracking implements ResultHandler {
		private MockHttpSession lastSession;

		@Override
		public void handle(MvcResult result) throws Exception {
			lastSession = (MockHttpSession) result.getRequest().getSession(false);
		}

		public MockHttpSession getLastSession() {
			return lastSession;
		}
	}

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	private MockMvc mockMvc;

	private SessionTracking sessions;

	private static final String CLIENT_ID = "auth_server";
	private static final String CLIENT_SECRET = "secret";

	private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

	private static final String EMAIL = "jim@yahoo.com";
	private static final String NAME = "Jim";

	@Before
	public void setup() {
		sessions = new SessionTracking();
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain)
				.build();
//		HtmlUnitDriver driver = MockMvcHtmlUnitDriverBuilder
//			    .mockMvcSetup(mockMvc)
//			    .build();
	}

	@Test
	public void test() {
		try {
			obtainAuthorizationCode();
		} catch (Exception e) {
			fail();
		}
	}

	private void obtainAuthorizationCode() throws Exception {
		MockHttpSession session = new MockHttpSession();

		String responseType = "code";
		String scope = "create_oauth read_oauth update_oauth delete_oauth";
		String clientId = "auth_server";
		String redirectUrl = "http://localhost/redirect.html";

//		params.add("response_type", "code");
//		params.add("scope", "create_oauth read_oauth update_oauth delete_oauth");
//		params.add("client_id", CLIENT_ID);
//		params.add("redirect_uri", "http://localhost:8889/redirect.html");

		String url = "http://localhost/oauth/authorize"
				+ "?response_type=" + responseType
				+ "&scope=" + scope
				+ "&client_id=" + clientId
				+ "&redirect_uri=" + redirectUrl;

		MvcResult mvcResult = mockMvc.perform(get(url)
//				.params(params)
				.session(session))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("http://localhost/login"))
				.andReturn();

//		params.clear();
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("username", "eru");
		params.add("password", "kpass");

		mvcResult = mockMvc.perform(post("http://localhost/login")
				.params(params)
				.session(session))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl(url.replace(" ", "%20")))
				.andReturn();

		params.clear();
		params.add("user_oauth_approval", "true");
		params.add("scope.create_oauth", "true");
		params.add("scope.read_oauth", "true");
		params.add("scope.update_oauth", "true");
		params.add("scope.delete_oauth", "true");
		params.add("authorize", "Authorize");

		mvcResult = mockMvc.perform(post(url)
				.session(session)
				.params(params))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl(redirectUrl))
				.andReturn();

		String resultString = mvcResult.getResponse().getRedirectedUrl();
//		session = (MockHttpSession) mvcResult.getRequest().getSession();
//
//		params.clear();
//		params.add("response_type", "code");
//		params.add("scope", "create_oauth read_oauth update_oauth delete_oauth");
//		params.add("client_id", CLIENT_ID);
//		params.add("redirect_uri", "http://localhost:8889/redirect.html");
//
//		mvcResult = mockMvc.perform(get("http://localhost/oauth/authorize")
//				.params(params)
//				.accept(CONTENT_TYPE)) // TODO: set correct content type
//				.andExpect(status().isFound())
//				.andExpect(redirectedUrl("http://localhost:8889/redirect.html"))
//				.andReturn();
//		result = mockMvc.perform(post("/login")
//				.with(user("eru").password("kpass"))
////				.params(params)
////				.with(httpBasic("eru", "kpass"))
//				.accept(CONTENT_TYPE))
//				.andExpect(status().isFound())
//				.andExpect(redirectedUrl("http://localhost:8889/redirect.html"));

//		String resultString = result.andReturn().getResponse().getContentAsString();
		String breaka = "";
	}

	private String obtainAccessToken(String username, String password) throws Exception {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", CLIENT_ID);
		params.add("username", username);
		params.add("password", password);

		// @formatter:off

        ResultActions result = mockMvc.perform(post("/oauth/token")
                               .params(params)
                               .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
                               .accept(CONTENT_TYPE))
                               .andExpect(status().isOk())
                               .andExpect(content().contentType(CONTENT_TYPE));
        
        // @formatter:on

		String resultString = result.andReturn().getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();
	}

}
