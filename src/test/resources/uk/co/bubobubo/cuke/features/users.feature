Feature: rest operations for user

  Background: clean up before run
    Given bubobubo is running
    And sparqlr is running
    And I delete the test user and repo
    And I end the http session

  Scenario: create a user
    Given I start the http session
    When I post "json/test_user.json" to sparqlr "/rest/users" as unauth with
      | type   | name         | value            |
      | header | Content-Type | application/json |
    Then I should get a 200 response code
    And the response body JSON should match the file "json/test_user.json"

  Scenario: create a user returns 400 for null user
    Given I start the http session
    When I post "json/test_user_null.json" to sparqlr "/rest/users" as unauth with
      | type   | name         | value            |
      | header | Content-Type | application/json |
    Then I should get a 400 response code

  Scenario: update a user doesn't work when logged out
    When I put "json/test_user.json" to sparqlr "/rest/users" as unauth with
      | type   | name         | value            |
      | header | Content-Type | application/json |
    Then I should get a 401 response code

  Scenario: update a user
    Given I start the http session
    And I post "json/test_user.json" to sparqlr "/rest/users" as unauth with
      | type   | name         | value            |
      | header | Content-Type | application/json |
    And I should get a 200 response code
    When I put "json/test_user_update.json" to sparqlr "/rest/users" with
      | type   | name         | value            |
      | header | Content-Type | application/json |
    Then I should get a 200 response code
    And the response body JSON should match the file "json/test_user_updated.json"

