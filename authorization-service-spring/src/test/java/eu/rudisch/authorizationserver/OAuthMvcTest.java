package eu.rudisch.authorizationserver;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest // (classes = AuthorizationServerApplication.class)
@ActiveProfiles("mvc")
public class OAuthMvcTest {

	@RestController
	public class RedirectController {

		@RequestMapping(value = "/html", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
		@ResponseStatus(code = HttpStatus.OK)
		public String getAccount() {

			return "<!DOCTYPE html>\n" +
					"<html>\n" +
					"  <head>\n" +
					"    <title>Auth-Server-Redirect</title>\n" +
					"  </head>\n" +
					"  <body>\n" +
					"  </body>\n" +
					"</html>";
		}

	}

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(9999);

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	private MockMvc mockMvc;

	private static final String CLIENT_ID = "auth_server";
	private static final String CLIENT_SECRET = "secret";

	private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

	private static final String EMAIL = "jim@yahoo.com";
	private static final String NAME = "Jim";

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain)
				.build();
//		HtmlUnitDriver driver = MockMvcHtmlUnitDriverBuilder
//			    .mockMvcSetup(mockMvc)
//			    .build();
//		MediaType.TEXT_HTML_VALUE
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
		String htmlBody = "<!DOCTYPE html>\n" +
				"<html>\n" +
				"  <head>\n" +
				"    <title>Auth-Server-Redirect</title>\n" +
				"  </head>\n" +
				"  <body>\n" +
				"  </body>\n" +
				"</html>";

		stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(
				urlEqualTo("http://localhost:9999/redirect.html"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", MediaType.TEXT_HTML_VALUE)
						.withBody(htmlBody)));

//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		HttpGet request = new HttpGet(String.format("http://localhost:9999/redirect.html"));
//		HttpResponse httpResponse = httpClient.execute(request);
//
//		MvcResult mvcResult0 = mockMvc.perform(get("http://localhost:8889/redirect.html"))
////				.andExpect(content().contentType(MediaType.TEXT_HTML_VALUE))
//				.andExpect(status().isOk())
//				.andReturn();

		MockHttpSession session = new MockHttpSession();

		String responseType = "code";
		String scope = "create_oauth read_oauth update_oauth delete_oauth";
		String clientId = "auth_server";
		String redirectUrl = "http://localhost:8889/redirect.html";

//		params.add("response_type", "code");
//		params.add("scope", "create_oauth read_oauth update_oauth delete_oauth");
//		params.add("client_id", CLIENT_ID);
//		params.add("redirect_uri", "http://localhost:8889/redirect.html");

		String url = "http://localhost/oauth/authorize"
				+ "?response_type=" + responseType
				+ "&scope=" + scope
				+ "&client_id=" + clientId
				+ "&redirect_uri=" + redirectUrl;

//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		HttpGet request = new HttpGet(url);
//		HttpResponse httpResponse = httpClient.execute(request);

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
//				.params(params)
		)
//				.andExpect(status().isFound())
//				.andExpect(redirectedUrl(redirectUrl))
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
