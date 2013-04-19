package uk.co.bubobubo.cuke.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.PendingException;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import uk.co.bubobubo.cuke.utils.HttpUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BuboStepDefs {

    @Given("^bubobubo is running$")
    public void bubobubo_is_running() throws Throwable {

        HttpResponse response = HttpUtils.httpGet("");

        assertEquals(200, response.getStatusLine().getStatusCode());

    }

    @And("^I create the test user$")
    public void I_create_the_test_user() throws Throwable {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> headers = new HashMap<String, String>();

		params.put("username", "semuser");
		params.put("password", "webpass");
		params.put("reponame", "reponame");
		params.put("repopass", "repopass");

		headers.put("Content-type", "application/x-www-form-urlencoded");

		// create user and repo
		HttpResponse httpResponse = HttpUtils.httpPost("/user", params, headers);
		assertEquals(EntityUtils.toString(httpResponse.getEntity()), 201, httpResponse.getStatusLine().getStatusCode());
    }

    @And("^I create the test repo$")
    public void I_create_the_test_repo() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @When("^I get \"([^\"]*)\"$")
    public void I_get(String arg1) throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^I should get a (\\d+) response code$")
    public void I_should_get_a_response_code(int arg1) throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }
}
