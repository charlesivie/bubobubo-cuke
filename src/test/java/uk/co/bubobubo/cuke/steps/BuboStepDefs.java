package uk.co.bubobubo.cuke.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.PendingException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.custommonkey.xmlunit.XMLUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import uk.co.bubobubo.cuke.bean.RequestAttribute;
import uk.co.bubobubo.cuke.utils.HttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;

public class BuboStepDefs {

	private String testRepo = "test-repo-1";
	private String testRepoPass = "testrepopass";

	private HttpResponse response;

	private String testRepoDesc = "test-repo-one user by Cucumber JVM";

	@Given("^bubobubo and sparqlr are running$")
	public void bubobubo_and_sparqlr_are_running() throws Throwable {

		response = HttpUtils.httpGet(bubobuboUrl + "/protocol", new ArrayList<RequestAttribute>());

		assertEquals(200, response.getStatusLine().getStatusCode());

		response = HttpUtils.httpGet(sparqlrUrl, new ArrayList<RequestAttribute>());

		assertEquals(200, response.getStatusLine().getStatusCode());

	}

	@When("^I get \"([^\"]*)\" with$")
	public void I_get_with(String path, List<RequestAttribute> params) throws Throwable {
		// Express the Regexp above with the code you wish you had
		String urlWithCredentials = bubobuboUrl.replace("http://", "http://" + testRepo + ":" + testRepoPass + "@") + path;
		response = HttpUtils.httpGet(urlWithCredentials, params);
	}

	@When("^I post to \"([^\"]*)\" with$")
	public void I_post_with(String path, List<RequestAttribute> params) throws Throwable {
		// Express the Regexp above with the code you wish you had
		String urlWithCredentials = bubobuboUrl.replace("http://", "http://" + testRepo + ":" + testRepoPass + "@") + path;
		response = HttpUtils.httpPost(urlWithCredentials, params);
	}


	@When("^I get \"([^\"]*)\" as unauthorised with$")
	public void I_get_unauth(String path, List<RequestAttribute> params) throws Throwable {
		// Express the Regexp above with the code you wish you had
		String urlWithCredentials = bubobuboUrl + path;
		response = HttpUtils.httpGet(urlWithCredentials, params);
	}


	@And("^I delete the test user and repo$")
	public void I_delete_the_test_user_and_repo() throws Throwable {

		String username = "test@user.com";
		String password = "password";
		response = HttpUtils.httpDelete(sparqlrUrl + "/account/?username=" + username + "&password=" + password);
		assertEquals(HttpUtils.responseAsString, 200, response.getStatusLine().getStatusCode());

	}


	@And("^Passwords do not match on create$")
	public void Passwords_dont_match() throws Throwable {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> headers = new HashMap<String, String>();

		params.put("email", "test@user.com");
		params.put("user.password", "password");
		params.put("user.passwordConfirm", "wrong");

		// create user and repo
		response = HttpUtils.httpPost(sparqlrUrl + "/account", params, headers);
		assertEquals(HttpUtils.responseAsString, 200, response.getStatusLine().getStatusCode());
	}


	@And("^I create the test user and repo$")
	public void I_create_the_test_user_and_repo() throws Throwable {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> headers = new HashMap<String, String>();

		params.put("company", "Sparqlr");
		params.put("lastName", "ivie");
		params.put("firstName", "charlie");
		params.put("email", "test@user.com");
		params.put("user.password", "password");
		params.put("user.passwordConfirm", "password");

		// create user and repo
		response = HttpUtils.httpPost(sparqlrUrl + "/account", params, headers);
		assertEquals(HttpUtils.responseAsString, 200, response.getStatusLine().getStatusCode());

		params = new HashMap<String, Object>();
		params.put("name", testRepo);
		params.put("description", testRepoDesc);
		params.put("password", testRepoPass);

		response = HttpUtils.httpPost(sparqlrUrl + "/repositories/repository", params, headers);
		assertEquals(HttpUtils.responseAsString, 200, response.getStatusLine().getStatusCode());

	}

	@And("^I start the http session$")
	public void http_session_start() throws Throwable {

		HttpUtils.startSession();

	}

	@And("^I end the http session$")
	public void http_session_end() throws Throwable {

		HttpUtils.endSession();

	}

	@Then("^I should get a (\\d+) response code$")
	public void I_should_get_a_response_code(int code) throws Throwable {
		assertEquals(HttpUtils.responseAsString, code, response.getStatusLine().getStatusCode());
	}

	@And("^the response should be \"([^\"]*)\"$")
	public void the_response_should_be(String arg1) throws Throwable {
		assertEquals(arg1, HttpUtils.responseAsString);
	}

	@Then("^the response body should match the file \"([^\"]*)\"$")
	public void the_response_body_should_match_the_file(String classpathFileLocation) throws Throwable {
		File file = new ClassPathResource("expected/" + classpathFileLocation).getFile();

		String expected = FileUtils.readFileToString(file);
		String actual = HttpUtils.responseAsString;

		if (classpathFileLocation.toLowerCase().endsWith(".xml")) {
			XMLUnit.setNormalizeWhitespace(true);
			XMLUnit.setIgnoreComments(true);
			XMLUnit.setIgnoreAttributeOrder(true);
			assertXMLEqual(expected, actual);
		} else {
			assertEquals(expected, actual);
		}

	}


	private static String bubobuboUrl;

	@Value("${bubobubo.url}")
	public void setBubobuboUrl(String url) {
		bubobuboUrl = url;
	}

	private static String sparqlrUrl;

	@Value("${sparqlr.url}")
	public void setSparqlrUrl(String url) {
		sparqlrUrl = url;
	}

    @When("^I delete \"([^\"]*)\"$")
    public void I_delete(String path) throws Throwable {
        String urlWithCredentials = bubobuboUrl.replace("http://", "http://" + testRepo + ":" + testRepoPass + "@") + path;
        response = HttpUtils.httpDelete(urlWithCredentials);
    }
}
