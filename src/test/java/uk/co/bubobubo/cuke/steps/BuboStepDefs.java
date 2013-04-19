package uk.co.bubobubo.cuke.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.PendingException;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import uk.co.bubobubo.cuke.utils.HttpUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BuboStepDefs {

	private String testRepo = "testrepo";
	private String testRepoPass = "testrepopass";

	private HttpResponse response;

	@Given("^bubobubo and sparqlr are running$")
    public void bubobubo_and_sparqlr_are_running() throws Throwable {

        response = HttpUtils.httpGet(bubobuboUrl);

        assertEquals(200, response.getStatusLine().getStatusCode());

		response = HttpUtils.httpGet(sparqlrUrl);

		assertEquals(200, response.getStatusLine().getStatusCode());

    }


	@And("^I delete the test user and repo$")
	public void I_delete_the_test_user_and_repo() throws Throwable {

		String username = "test@user.com";
		String password = "password";
		response = HttpUtils.httpDelete(sparqlrUrl + "/account/?username=" + username +"&password="+password);
		assertEquals(EntityUtils.toString(response.getEntity()), 200, response.getStatusLine().getStatusCode());

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
		assertEquals(EntityUtils.toString(response.getEntity()), 200, response.getStatusLine().getStatusCode());
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
		assertEquals(EntityUtils.toString(response.getEntity()), 302, response.getStatusLine().getStatusCode());
    }


	@When("^I get \"([^\"]*)\" as test user$")
	public void I_get_as_user(String uri) throws Throwable {

		String urlWithCredentials = bubobuboUrl.replace("http://", "http://"+testRepo+":"+testRepoPass+"@");

		response = HttpUtils.httpGet(urlWithCredentials + uri);
		assertEquals(EntityUtils.toString(response.getEntity()), 200, response.getStatusLine().getStatusCode());
	}

    @Then("^I should get a (\\d+) response code$")
    public void I_should_get_a_response_code(int arg1) throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
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
}
