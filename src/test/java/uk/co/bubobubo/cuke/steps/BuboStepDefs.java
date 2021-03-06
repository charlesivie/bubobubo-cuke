package uk.co.bubobubo.cuke.steps;

import com.jayway.restassured.path.json.JsonPath;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.PendingException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.custommonkey.xmlunit.XMLUnit;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import uk.co.bubobubo.cuke.bean.RequestAttribute;
import uk.co.bubobubo.cuke.utils.HttpUtils;

import java.io.File;
import java.util.*;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BuboStepDefs {

	private String testRepo = "test-repo-1";
	private String testRepoPass = "testrepopass";

	private HttpResponse response;

	private String testRepoDesc = "test-repo-one user by Cucumber JVM";
    private String user = "test@user.com";
    private String password = "password";

    @Given("^bubobubo and sparqlr are running$")
	public void bubobubo_and_sparqlr_are_running() throws Throwable {

		response = HttpUtils.httpGet(bubobuboUrl + "/protocol", new ArrayList<RequestAttribute>());

		assertEquals(200, response.getStatusLine().getStatusCode());

		response = HttpUtils.httpGet(sparqlrUrl, new ArrayList<RequestAttribute>());

		assertEquals(200, response.getStatusLine().getStatusCode());

	}

    @Given("^bubobubo is running$")
    public void bubobubo_is_running() throws Throwable {

        response = HttpUtils.httpGet(bubobuboUrl + "/protocol", new ArrayList<RequestAttribute>());
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Given("^sparqlr is running$")
    public void sparqlr_is_running() throws Throwable {

        response = HttpUtils.httpGet(sparqlrUrl, new ArrayList<RequestAttribute>());
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @When("^I post the following form parameters to \"([^\"]*)\":$")
    public void I_post_the_following_form_parameters_to_(String path, DataTable parameters) throws Throwable {

        List<Map<String, String>> rawData = parameters.asMaps();
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        for(Map<String, String> kvp : rawData) {
            parameterMap.put(kvp.get("name"), kvp.get("value"));
        }
        response = HttpUtils.httpPost(sparqlrUrl + path, parameterMap, new HashMap<String, String>());
    }

    @When("^I get \"([^\"]*)\"$")
    public void I_get(String path) throws Throwable {
        String urlWithCredentials = bubobuboUrl.replace("http://", "http://" + testRepo + ":" + testRepoPass + "@") + path;
        response = HttpUtils.httpGet(urlWithCredentials, Collections.<RequestAttribute>emptyList());
    }

    @When("^I get \"([^\"]*)\" from sparqlr$")
    public void I_get_sparqlr(String path) throws Throwable {

        String urlWithCredentials = sparqlrUrl + path;
        response = HttpUtils.httpGet(urlWithCredentials, Collections.<RequestAttribute>emptyList());
    }

	@When("^I get \"([^\"]*)\" with$")
	public void I_get_with(String path, List<RequestAttribute> params) throws Throwable {

        String urlWithCredentials = bubobuboUrl.replace("http://", "http://" + testRepo + ":" + testRepoPass + "@") + path;
		response = HttpUtils.httpGet(urlWithCredentials, params);
	}


	@When("^I put to \"([^\"]*)\" on sparqlr$")
	public void I_put(String path) throws Throwable {
		response = HttpUtils.httpPut(sparqlrUrl + path );
	}

	@When("^I post to \"([^\"]*)\" with$")
	public void I_post_with(String path, List<RequestAttribute> params) throws Throwable {

        String urlWithCredentials = bubobuboUrl.replace("http://", "http://" + testRepo + ":" + testRepoPass + "@") + path;
		response = HttpUtils.httpPost(urlWithCredentials, params);
	}

    @When("^I post \"([^\"]*)\" to \"([^\"]*)\" with$")
    public void I_post_to_with(String fileLocation, String path, List<RequestAttribute> params) throws Throwable {

        String urlWithCredentials = bubobuboUrl.replace("http://", "http://" + testRepo + ":" + testRepoPass + "@") + path;
        response = HttpUtils.httpPost(urlWithCredentials, params, fileLocation);
    }

    @When("^I post \"([^\"]*)\" to sparqlr \"([^\"]*)\" with$")
    public void I_post_to_sparqlr_with(String fileLocation, String path, List<RequestAttribute> params) throws Throwable {

        String urlWithCredentials = sparqlrUrl + path;
        response = HttpUtils.httpPost(urlWithCredentials, params, fileLocation);
    }

    @When("^I put \"([^\"]*)\" to sparqlr \"([^\"]*)\" with$")
    public void I_put_to_sparqlr_with(String fileLocation, String path, List<RequestAttribute> params) throws Throwable {

        String urlWithCredentials = sparqlrUrl + path;
        response = HttpUtils.httpPut(urlWithCredentials, params, fileLocation);
    }

    @When("^I post \"([^\"]*)\" to sparqlr \"([^\"]*)\" as unauth with$")
    public void I_post_to_unauth(String fileLocation, String path, List<RequestAttribute> params) throws Throwable {

        String urlWithCredentials = sparqlrUrl + path;
        response = HttpUtils.httpPost(urlWithCredentials, params, fileLocation);
    }

    @When("^I put \"([^\"]*)\" to sparqlr \"([^\"]*)\" as unauth with$")
    public void I_put_to_unauth(String fileLocation, String path, List<RequestAttribute> params) throws Throwable {

        String urlWithCredentials = sparqlrUrl + path;
        response = HttpUtils.httpPut(urlWithCredentials, params, fileLocation);
    }

	@When("^I get \"([^\"]*)\" as unauthorised with$")
	public void I_get_unauth(String path, List<RequestAttribute> params) throws Throwable {

        String urlWithCredentials = bubobuboUrl + path;
		response = HttpUtils.httpGet(urlWithCredentials, params);
	}


	@And("^I delete the test user and repo$")
	public void I_delete_the_test_user_and_repo() throws Throwable {

		String username = "test@user.com";
		String password = "password";
		response = HttpUtils.httpDelete(sparqlrUrl + "/account/?username=" + username + "&password=" + password);
		assertEquals(200, response.getStatusLine().getStatusCode());

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
		assertEquals(200, response.getStatusLine().getStatusCode());
	}


	@And("^I create the test user and repo$")
	public void I_create_the_test_user_and_repo() throws Throwable {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> headers = new HashMap<String, String>();

		params.put("company", "Sparqlr");
		params.put("lastName", "ivie");
		params.put("firstName", "charlie");
        params.put("email", user);
        params.put("user.password", password);
		params.put("user.passwordConfirm", password);

		// create user and repo
		response = HttpUtils.httpPost(sparqlrUrl + "/account", params, headers);
		assertEquals(200, response.getStatusLine().getStatusCode());

		params = new HashMap<String, Object>();
		params.put("name", testRepo);
		params.put("description", testRepoDesc);
		params.put("password", testRepoPass);

		response = HttpUtils.httpPost(sparqlrUrl + "/repositories/repository", params, headers);
		assertEquals(200, response.getStatusLine().getStatusCode());

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
		assertEquals("response: "+HttpUtils.responseAsString, code, response.getStatusLine().getStatusCode());
	}

	@And("^the response should be \"([^\"]*)\"$")
	public void the_response_should_be(String responseAsString) throws Throwable {
		assertEquals(responseAsString, HttpUtils.responseAsString);
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

    @Then("^the response body should contain the string \"([^\"]*)\"$")
    public void the_response_body_should_contain_the_string(String testString) throws Throwable {

        assertTrue(HttpUtils.responseAsString.contains(testString));
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

    @When("^I delete \"([^\"]*)\" from sparqlr$")
    public void I_delete_from_sparqlr(String path) throws Throwable {
        String urlWithCredentials = sparqlrUrl+path;
        response = HttpUtils.httpDelete(urlWithCredentials);
    }

    @When("^I get \"([^\"]*)\" as user with$")
    public void I_get_as_user_with(String path, List<RequestAttribute> params) throws Throwable {
		Credentials defaultcreds = new UsernamePasswordCredentials(user, password);

        String url = bubobuboUrl + path;
        response = HttpUtils.httpGet(url, defaultcreds, params);
    }

    @And("^I create test repo (\\d+)$")
    public void I_create_test_repo(int repoNumber) throws Throwable {
        // Express the Regexp above with the code you wish you had

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, String> headers = new HashMap<String, String>();

        params.put("name", testRepo+repoNumber);
        params.put("description", testRepoDesc+repoNumber);
        params.put("password", testRepoPass+repoNumber);

        response = HttpUtils.httpPost(sparqlrUrl + "/repositories/repository", params, headers);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @When("^I get \"([^\"]*)\" as repo (\\d+) with$")
    public void I_get_as_repo_with(String path, int repoNumber, List<RequestAttribute> params) throws Throwable {
        // Express the Regexp above with the code you wish you had
        String urlWithCredentials = bubobuboUrl.replace("http://", "http://" + testRepo+repoNumber + ":" + testRepoPass +repoNumber+ "@") + path;
        response = HttpUtils.httpGet(urlWithCredentials, params);
    }

	@And("^the response body JSON should match the file \"([^\"]*)\"$")
	public void the_response_body_JSON_should_match_the_file(String path) throws Throwable {

		File file = new ClassPathResource("expected/" + path).getFile();

		String expected = FileUtils.readFileToString(file);
		String actual = HttpUtils.responseAsString;

		JSONAssert.assertEquals(expected, actual, false);
	}

    @Given("^not yet implemented$")
    public void not_yet_implemented() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new Exception("Implement me");
    }

    @And("^the response json path \"([^\"]*)\" should match \"([^\"]*)\"$")
    public void the_response_json_path_should_match(String path, String expected) throws Throwable {
        assertEquals(expected, String.valueOf(JsonPath.given(HttpUtils.responseAsString).get(path)));
    }
}
