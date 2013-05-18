Feature: query for serql responses

  Background: setup
    Given bubobubo is running
    And sparqlr is running
	And I delete the test user and repo
	And I end the http session

  Scenario: signup create repo and perform serql query
	Given I start the http session
	And I create the test user and repo
	When I get "/repositories/test-repo-1" with
	  | type      | name    | value                                     |
	  | parameter | query   | select <foo:bar>                          |
	  | parameter | queryLn | serql                                     |
	  | header    | accept  | application/sparql-results+xml, */*;q=0.5 |
	Then I should get a 200 response code
	And the response body should match the file "serql/foo_bar.xml"

  Scenario: I clean up