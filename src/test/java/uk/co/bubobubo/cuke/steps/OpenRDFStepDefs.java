package uk.co.bubobubo.cuke.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import org.openrdf.query.*;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriterRegistry;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPGraphQuery;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.http.HTTPTupleQuery;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriterRegistry;
import uk.co.bubobubo.cuke.bean.RequestAttribute;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class OpenRDFStepDefs {

	private String repositoryBaseUri = "http://localhost:8080/openrdf-sesame/";
	private String username = "test@user.com";
	private String password = "password";

	private boolean askResult;
	private QueryResult queryResult;
	private String resultAsString;


	@And("^the number of explicit triples in from \"([^\"]*)\" is (\\d+)$")
	public void the_number_of_explicit_triples_in_from_is(String repositoryId, int count) throws Throwable {
		RepositoryConnection connection = connect(repositoryId);
		assertEquals(count, connection.size());
	}

	@When("^I use open-rdf libs to SELECT from \"([^\"]*)\" with the parameters$")
	public void I_use_open_rdf_libs_to_SELECT_from_with_the_parameters(String repositoryId, List<RequestAttribute> params)
			throws Throwable {

		String query = getParameter("query", params);
		String accept = getHeader("accept", params);

		RepositoryConnection connection = connect(repositoryId);

		// Create a specific HTTP flavour tuple query
		HTTPTupleQuery tupleQuery =
				(HTTPTupleQuery)connection.prepareTupleQuery(QueryLanguage.SPARQL, query);

		// Somewhere to collect the output
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		if(accept == null) {
			queryResult = tupleQuery.evaluate();
		} else {
			// Prepare a writer to produce a single String of results
			TupleQueryResultWriter tupleQueryResultWriter =
				TupleQueryResultWriterRegistry.getInstance()
						.get(TupleQueryResultFormat.JSON)   //   Write results as JSON
						.getWriter(baos);                   //   Dump results to outputStream
			// run the query
			tupleQuery.evaluate(tupleQueryResultWriter);
			resultAsString = baos.toString();
		}
	}

	// A fairly specific case
	@When("^I use open-rdf libs to ASK from \"([^\"]*)\" with the parameters$")
	public void I_use_open_rdf_libs_to_ASK_from_with_the_parameters(String repositoryId, List<RequestAttribute> params)
			throws Throwable {

		String query = getParameter("query", params);
		String accept = getHeader("accept", params);

		RepositoryConnection connection = connect(repositoryId);

		// prepare the query
		BooleanQuery booleanQuery = connection.prepareBooleanQuery(QueryLanguage.SPARQL, query);

		// Evaluate the query
		askResult = booleanQuery.evaluate();
	}

	@When("^I use open-rdf libs to CONSTRUCT from \"([^\"]*)\" with the parameters$")
	public void I_use_open_rdf_libs_to_CONSTRUCT_from_with_the_parameters(String repositoryId, List<RequestAttribute> params)
			throws Throwable {

		String query = getParameter("query", params);
		String accept = getHeader("accept", params);

		RepositoryConnection connection = connect(repositoryId);

		// Create a specific HTTP flavour tuple query
		HTTPGraphQuery graphQuery =
				(HTTPGraphQuery)connection.prepareGraphQuery(QueryLanguage.SPARQL, query);

		if(accept == null) {
			queryResult = graphQuery.evaluate();
		} else {
            // Somewhere to collect the output
            StringWriter writer = new StringWriter();

			// Prepare a writer to produce a single String of results
			RDFHandler rdfHandler = RDFWriterRegistry.getInstance()
				.get(RDFFormat.TURTLE)      // output in Turtle
				.getWriter(writer);         // dump output to writer

			// run the query
			graphQuery.evaluate(rdfHandler);
			resultAsString = writer.toString();
		}

	}

    Query prepareQuery(Class<? extends Query> queryType,
        RepositoryConnection connection,
        QueryLanguage queryLanguage,
        String query) {

        return null;
    }

    private QueryResult prepareQuery(RepositoryConnection connection, String query) {
        return prepareQuery(connection, QueryLanguage.SPARQL, query);
    }

    private QueryResult prepareQuery(RepositoryConnection connection, QueryLanguage queryLanguage, String query) {
        return null;
    }

    private String queryResultAsString(Class<? extends Query> queryType, Query query)
            throws QueryEvaluationException, RDFHandlerException, TupleQueryResultHandlerException {


        if(queryType.isInstance(GraphQuery.class)) {
            // Somewhere to collect the output
            StringWriter writer = new StringWriter();

            // Prepare a writer to produce a single String of results
            RDFHandler rdfHandler = RDFWriterRegistry.getInstance()
                .get(RDFFormat.TURTLE)      // output in Turtle
                .getWriter(writer);         // dump output to writer

            // run the query
            ((GraphQuery)query).evaluate(rdfHandler);
            return writer.toString();
        } else if(queryType.isInstance(TupleQuery.class)) {

            // Somewhere to collect the output
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Prepare a writer to produce a single String of results
            TupleQueryResultWriter tupleQueryResultWriter =
                    TupleQueryResultWriterRegistry.getInstance()
                            .get(TupleQueryResultFormat.JSON)   //   Write results as JSON
                            .getWriter(baos);                   //   Dump results to outputStream
            // run the query
            ((TupleQuery)query).evaluate(tupleQueryResultWriter);
            return baos.toString();
        }
        return null;
    }

	private void doInSesame(String repositoryId, List<RequestAttribute> params, Class<? extends Query> queryType)
            throws Throwable {

		String query = getParameter("query", params);
		String accept = getHeader("accept", params);

		RepositoryConnection connection = connect(repositoryId);

		// query = prepareQuery(queryType, connection, queryLanguage, query);

		if(accept == null) {
			// use default format
			// queryResult = query.evaluate();
		} else {
			// set result format accordingly
			// resultAsString = getResult(...);
		}
	}

	private RepositoryConnection connect(final String repositoryId) throws Exception {
		// Make an HTTP connection to the repository
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


	public void setRepositoryBaseUri(String repositoryBaseUri) {
		this.repositoryBaseUri = repositoryBaseUri;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
