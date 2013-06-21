package uk.co.bubobubo.cuke.utils.querystrategy;

import org.openrdf.query.*;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriterRegistry;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriterRegistry;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

public class QueryStrategyFactory {

    public static QueryStrategy constructQueryStrategy() {
        return new ConstructQueryStrategy();
    }

    public static QueryStrategy selectQueryStrategy() {
        return new SelectQueryStrategy();
    }

    public static QueryStrategy askQueryStrategy() {
        return new AskQueryStrategy();
    }
}

class AskQueryStrategy implements QueryStrategy {

    @Override
    public BooleanQuery prepareQuery(RepositoryConnection connection, QueryLanguage queryLanguage, String query)
            throws MalformedQueryException, RepositoryException {
        return connection.prepareBooleanQuery(queryLanguage, query);
    }

    @Override
    public QueryResult evaluateQuery(Query query) throws QueryEvaluationException {
        return new BooleanQueryResult(((BooleanQuery) query).evaluate());
    }

    @Override
    public String queryResultAsString(Query query, String outputFormat)
            throws QueryEvaluationException, TupleQueryResultHandlerException, RDFHandlerException {
        return String.valueOf(((BooleanQuery) query).evaluate());
    }
}

// Feels a bit shoe-horned
class BooleanQueryResult implements QueryResult<Boolean> {

    private final boolean result;
    private boolean called = false;

    BooleanQueryResult(final boolean result) {
        this.result = result;
    }

    @Override
    public void close() throws QueryEvaluationException {}

    @Override
    public boolean hasNext() throws QueryEvaluationException {
        return !called;
    }

    @Override
    public Boolean next() throws QueryEvaluationException {
        called = true;
        return result;
    }

    @Override
    public void remove() throws QueryEvaluationException {}
}

class ConstructQueryStrategy implements QueryStrategy {

    @Override
    public GraphQuery prepareQuery(RepositoryConnection connection, QueryLanguage queryLanguage, String query)
            throws MalformedQueryException, RepositoryException {
        return connection.prepareGraphQuery(queryLanguage, query);
    }

    @Override
    public GraphQueryResult evaluateQuery(Query query) throws QueryEvaluationException {
        return ((GraphQuery)query).evaluate();
    }

    @Override
    public String queryResultAsString(Query query, String outputFormat)
            throws QueryEvaluationException, RDFHandlerException {

        RDFFormat rdfFormat = RDFFormat.forMIMEType(outputFormat, RDFFormat.TURTLE);
        StringWriter writer = new StringWriter();

        RDFHandler rdfHandler = RDFWriterRegistry.getInstance()
                .get(rdfFormat)
                .getWriter(writer);

        ((GraphQuery)query).evaluate(rdfHandler);
        return writer.toString();
    }
}

class SelectQueryStrategy implements QueryStrategy {

    @Override
    public TupleQuery prepareQuery(RepositoryConnection connection, QueryLanguage queryLanguage, String query)
            throws MalformedQueryException, RepositoryException {
        return connection.prepareTupleQuery(queryLanguage, query);
    }

    @Override
    public TupleQueryResult evaluateQuery(Query query) throws QueryEvaluationException {
        return ((TupleQuery)query).evaluate();
    }

    @Override
    public String queryResultAsString(Query query, String outputFormat)
            throws QueryEvaluationException, TupleQueryResultHandlerException {

        TupleQueryResultFormat format = TupleQueryResultFormat.forMIMEType(outputFormat, TupleQueryResultFormat.JSON);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TupleQueryResultWriter tupleQueryResultWriter =
                TupleQueryResultWriterRegistry.getInstance()
                        .get(format)
                        .getWriter(baos);
        ((TupleQuery)query).evaluate(tupleQueryResultWriter);
        return baos.toString();
    }
}
