@Wip
Feature: Retrieve repository listing

  Background: setup
    Given bubobubo is running
    And sparqlr is running
    And I delete the test user and repo
    And I end the http session

  Scenario: Retrieve repository listing as authorised
    Given I start the http session
    And I create the test user and repo
    When I get "/repositories"
    Then I should get a 204 response code
    And I end the http session

  Scenario: Retrieve repository listing as unauthorised
    Given I start the http session
    And I end the http session
    When I get "/repositories"
    Then I should get a 401 response code

  Scenario: I clean up