Feature: query using open-rdf api

  Background: setup
	Given bubobubo and sparqlr are running
	And I delete the test user and repo
	And I end the http session

  Scenario: signup create repo and query through rest
	Given I start the http session
	And I create the test user and repo
	When I use open-rdf libs to SELECT from "test-repo-1" with the parameters
	  | type      | name   | value                                     |
	  | parameter | query  | select * where { ?s ?p ?o }               |
    Then I should get an empty response with no errors

  Scenario: signup create repo and query through rest without accept header
	Given I start the http session
	And I create the test user and repo
    When I use open-rdf libs to SELECT from "test-repo-1" with the parameters
	  | type      | name  | value                       |
	  | parameter | query | select * where { ?s ?p ?o } |
	Then I should get an empty resultset with no errors

  Scenario: signup create repo and perform ask query
	Given I start the http session
	And I create the test user and repo
    When I use open-rdf libs to ASK from "test-repo-1" with the parameters
	  | type      | name   | value                   |
	  | parameter | query  | ask { ?s ?p ?o }        |
	And the string result should be "true"

  Scenario: signup create repo and perform construct sparql query
	Given I start the http session
	And I create the test user and repo
    When I use open-rdf libs to CONSTRUCT from "test-repo-1" with the parameters
	  | type      | name         | value                                 |
	  | parameter | query        | construct {?s ?p ?o} where {?s ?p ?o} |
	Then the result should match the file "rdf/construct.ttl"

  Scenario: execute insert/delete to repo
    Given I start the http session
    And I create the test user and repo
    When I use open-rdf libs to UPDATE "test-repo-1" with the query "sparql/insert.sparql"
    Then I should have inserted into "test-repo-1" the triples
      | subject                | predicate                                 | object       |
      | <http://example/book1> | <http://purl.org/dc/elements/1.1/title>   | "A new book" |
      | <http://example/book1> | <http://purl.org/dc/elements/1.1/creator> | "A.N.Other"  |
    When I use open-rdf libs to UPDATE "test-repo-1" with the query "sparql/delete.sparql"
    Then I should have deleted from "test-repo-1" the triples
      | subject                | predicate                                 | object       |
      | <http://example/book1> | <http://purl.org/dc/elements/1.1/title>   | "A new book" |
      | <http://example/book1> | <http://purl.org/dc/elements/1.1/creator> | "A.N.Other"  |

  Scenario: I clean up
