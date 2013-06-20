@Wip
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
	  | header    | Accept | application/sparql-results+xml, */*;q=0.5 |
	Then I should get a 200 response code

  Scenario: signup create repo and query through rest without accept header
	Given I start the http session
	And I create the test user and repo
    When I use open-rdf libs to SELECT from "test-repo-1" with the parameters
	  | type      | name  | value                       |
	  | parameter | query | select * where { ?s ?p ?o } |
	Then I should get a 200 response code

  Scenario: signup create repo and perform ask query
	Given I start the http session
	And I create the test user and repo
    When I use open-rdf libs to ASK from "test-repo-1" with the parameters
	  | type      | name   | value                   |
	  | parameter | query  | ask { ?s ?p ?o }        |
	  | header    | Accept | text/boolean, */*;q=0.5 |
	And the response should be "true"

  Scenario: signup create repo and perform construct sparql query
	Given I start the http session
	And I create the test user and repo
    When I use open-rdf libs to CONSTRUCT from "test-repo-1" with the parameters
	  | type      | name         | value                                 |
	  | parameter | query        | construct {?s ?p ?o} where {?s ?p ?o} |
	  | header    | accept       | application/rdf+xml, */*;q=0.5        |
	  | header    | Content-Type | application/x-www-form-urlencoded     |
	Then I should get a 200 response code
	And the response body should match the file "sparql/construct.xml"

  Scenario: I clean up
