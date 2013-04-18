package com.bubobubo.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.PendingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.Assert.assertEquals;

public class BuboStepDefs {

    @Value("${bubobubo.url}")
    private String bubobuboUrl;

    @Given("^bubobubo is running$")
    public void bubobubo_is_running() throws Throwable {

        HttpClient client = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet("http://targethost/homepage");

        HttpResponse response = client.execute(httpGet);

        assertEquals(200, response.getStatusLine().getStatusCode());

    }

    @And("^I create the test user$")
    public void I_create_the_test_user() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
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
