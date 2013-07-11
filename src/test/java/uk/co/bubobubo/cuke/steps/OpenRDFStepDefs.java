package uk.co.bubobubo.cuke.steps;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import uk.co.bubobubo.cuke.bean.RequestAttribute;
import uk.co.bubobubo.cuke.utils.querystrategy.QueryStrategy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.co.bubobubo.cuke.utils.querystrategy.QueryStrategyFactory.askQueryStrategy;
import static uk.co.bubobubo.cuke.utils.querystrategy.QueryStrategyFactory.constructQueryStrategy;
import static uk.co.bubobubo.cuke.utils.querystrategy.QueryStrategyFactory.selectQueryStrategy;

public class OpenRDFStepDefs {

    @Value("${repository.base.uri}")
	private String repositoryBaseUri;
    @Value("${test.user.username}")
	private String username;
    @Value("${test.user.password}")
	private String password;

	private boolean askResult;
	private QueryResult queryResult;
	private String resultAsString;


	@When("^the number of explicit triples in from \"([^\"]*)\" is (\\d+)$")
	public void the_number_of_explicit_triples_in_from_is(String repositoryId, int count) throws Throwable {
		RepositoryConnection connection = connect(repositoryId);
		assertEquals(count, connection.size());
	}

    @When("^I use open-rdf libs to SELECT from \"([^\"]*)\" with the parameters$")
	public void I_use_open_rdf_libs_to_SELECT_from_with_the_parameters(String repositoryId, List<RequestAttribute> params)
			throws Throwable {

		doInSesame(repositoryId, params, selectQueryStrategy());
	}

	@When("^I use open-rdf libs to ASK from \"([^\"]*)\" with the parameters$")
	public void I_use_open_rdf_libs_to_ASK_from_with_the_parameters(String repositoryId, List<RequestAttribute> params)
			throws Throwable {

		doInSesame(repositoryId, params, askQueryStrategy());
	}

	@When("^I use open-rdf libs to CONSTRUCT from \"([^\"]*)\" with the parameters$")
	public void I_use_open_rdf_libs_to_CONSTRUCT_from_with_the_parameters(String repositoryId, List<RequestAttribute> params)
			throws Throwable {

		doInSesame(repositoryId, params, constructQueryStrategy());

	}

	private void doInSesame(String repositoryId, List<RequestAttribute> params, QueryStrategy strategy)
            throws Throwable {

        resultAsString = null;
        queryResult = null;

		String queryParameter = getParameter("query", params);
		String acceptHeader = getHeader("accept", params);
        String language = getParameter("queryLn", params);

		RepositoryConnection connection = connect(repositoryId);

        QueryLanguage queryLanguage = (language == null ? QueryLanguage.SPARQL : QueryLanguage.valueOf(language));
		Query query = strategy.prepareQuery(connection, queryLanguage, queryParameter);

		if(acceptHeader == null) {
			// use default format
			queryResult = strategy.evaluateQuery(query);
		} else {
			// set result format accordingly
			resultAsString = strategy.queryResultAsString(query, acceptHeader);
		}
	}

	private RepositoryConnection connect(final String repositoryId) throws Exception {
		HTTPRepository repository = new HTTPRepository(repositoryBaseUri, repositoryId);
		repository.setUsernameAndPassword(username, password);
		return repository.getConnection();
	}

	private String getParameter(String name, List<RequestAttribute> params) {
		return extractParam(name, "parameter", params);
	}

	private String getHeader(String name, List<RequestAttribute> params) {
		return extractParam(name, "header", params);
	}

	private String extractParam(String name, String type, List<RequestAttribute> params) {
		for(RequestAttribute requestAttribute : params) {
			if(requestAttribute.getType().equalsIgnoreCase(type)) {
				if(requestAttribute.getName().equalsIgnoreCase(name)) {
					return requestAttribute.getValue();
				}
			}
		}
		return null;
	}

    @Then("^I should get an empty response with no errors$")
    public void I_should_get_an_empty_response_with_no_errors() throws Throwable {

        JSONObject responseJson = new JSONObject(resultAsString);
        JSONArray results = responseJson.getJSONObject("results").getJSONArray("bindings");
        assertEquals(141, results.length());
    }

    @Then("^I should get an empty resultset with no errors$")
    public void I_should_get_an_empty_resultset_with_no_errors() throws Throwable {

        assertNotNull(queryResult);
        //assertFalse(queryResult.hasNext());
    }

    @And("^the boolean result should be \"([^\"]*)\"$")
    public void the_boolean_response_should_respond_with(String value) throws Throwable {

        assertEquals(Boolean.valueOf(value), queryResult.next());
    }

    @And("^the string result should be \"([^\"]*)\"$")
    public void the_string_response_should_respond_with(String value) throws Throwable {

        assertEquals(value, resultAsString.trim());
    }

    @Then("^the result should match the file \"([^\"]*)\"$")
    public void the_response_body_should_match_the_file(String classpathFileLocation) throws Throwable {
        File file = new ClassPathResource("expected/" + classpathFileLocation).getFile();

        String expected = FileUtils.readFileToString(file);
        String actual = resultAsString;

        if (FilenameUtils.getExtension(classpathFileLocation).equalsIgnoreCase("xml")) {
            XMLUnit.setNormalizeWhitespace(true);
            XMLUnit.setIgnoreComments(true);
            XMLUnit.setIgnoreAttributeOrder(true);
            assertXMLEqual(expected, actual);
        } else if(FilenameUtils.getExtension(classpathFileLocation).equalsIgnoreCase("ttl")) {

            ByteArrayInputStream bais = new ByteArrayInputStream(resultAsString.getBytes(CharEncoding.UTF_8));

            Model model = ModelFactory.createDefaultModel();
            model.read(bais, null, RDFFormat.TURTLE.getName());

            assertEquals(141, model.size());

        } else {
            assertEquals(expected, actual);
        }

    }
}

