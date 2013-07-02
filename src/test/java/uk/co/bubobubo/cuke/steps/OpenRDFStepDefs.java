package uk.co.bubobubo.cuke.steps;

import cucumber.api.java.en.When;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;
import org.springframework.beans.factory.annotation.Value;
import uk.co.bubobubo.cuke.bean.RequestAttribute;
import uk.co.bubobubo.cuke.utils.querystrategy.QueryStrategy;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.co.bubobubo.cuke.utils.querystrategy.QueryStrategyFactory.*;

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
}

