Feature: Contexts endpoint

  Background: setup
    Given bubobubo is running
    And sparqlr is running
	And I delete the test user and repo
	And I end the http session

    @Wip
  Scenario: Get a list of contexts
	Given I start the http session
	And I create the test user and repo
	When I get "/repositories/test-repo-1/contexts" with
	  | type   | name   | value                          |
	  | header | Accept | application/sparql-results+xml |
	Then I should get a 200 response code

    Scenario: I clean up