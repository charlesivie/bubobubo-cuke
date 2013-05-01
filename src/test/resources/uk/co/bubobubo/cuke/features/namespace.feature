Feature: namespaces endpoint

  Background: setup
	Given bubobubo and sparqlr are running
	And I delete the test user and repo
	And I end the http session

  Scenario: get all namespaces
	Given I start the http session
	And I create the test user and repo
	When I get "/repositories/test-repo-1/namespaces" with
	  | type   | name   | value                                     |
	  | header | accept | application/sparql-results+xml, */*;q=0.5 |
	Then I should get a 200 response code
	And the response body should match the file "sparql/namespaces.xml"

  Scenario: remove all namespaces
	Given I start the http session
	And I create the test user and repo
	When I delete "/repositories/test-repo-1/namespaces"
	Then I should get a 204 response code

  Scenario: I clean up