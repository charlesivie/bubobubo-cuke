Feature: repo list endpoint

  Background: setup
    Given bubobubo is running
    And sparqlr is running
	And I delete the test user and repo
	And I end the http session

  Scenario: Get a list of repos
	Given I start the http session
	And I create the test user and repo
	When I get "/repositories"
	Then I should get a 403 response code

  Scenario: I clean up