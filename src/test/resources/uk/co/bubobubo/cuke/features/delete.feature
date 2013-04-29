Feature: Delete is blocked through bubobubo

  Background: setup
    Given bubobubo and sparqlr are running
    And I delete the test user and repo
    And I end the http session

  Scenario: Try to delete a repository as authorised
    Given I start the http session
    And I create the test user and repo
    When I delete "/repositories/test-repo-1"
    Then I should get a 403 response code
	And I end the http session

  Scenario: Try to delete a repository as unauthorised
	Given I start the http session
	And I end the http session
	When I delete "/repositories/test-repo-1"
	Then I should get a 401 response code

  Scenario: I clean up