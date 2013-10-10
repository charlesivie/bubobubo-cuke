Feature: rest operations for user

  Background: clean up before run
    Given bubobubo is running
    And sparqlr is running
    And I delete the test user and repo
    And I end the http session

  Scenario: create a user
    Given I start the http session
    When I post "json/test_user.json" to sparqlr "/users" as unauth with
      | type   | name         | value            |
      | header | Content-Type | application/json |
    Then I should get a 200 response code
    And the response body JSON should match the file "json/test_user.json"


