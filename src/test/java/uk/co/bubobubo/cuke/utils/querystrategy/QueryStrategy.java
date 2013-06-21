package uk.co.bubobubo.cuke.utils.querystrategy;

import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

public interface QueryStrategy {

    Query prepareQuery(RepositoryConnection connection, QueryLanguage queryLanguage, String query)
            throws MalformedQueryException, RepositoryException;

    QueryResult evaluateQuery(Query query) throws QueryEvaluationException;

    String queryResultAsString(Query query, String outputFormat)
            throws QueryEvaluationException, TupleQueryResultHandlerException, RDFHandlerException;
}
