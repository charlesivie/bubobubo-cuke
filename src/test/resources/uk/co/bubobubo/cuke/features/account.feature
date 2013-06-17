@Wip
Feature: Account screen operations

  Scenario: Username/email address should be case-insensitive
    Given I post the following form parameters to "/account":
      | name                 | value                 |
      | company              | Sparqlr               |
      | lastName             | Coates                |
      | firstName            | Phil                  |
      | email                | test.user@sparqlr.com |
      | user.password        | password              |
      | user.passwordConfirm | password              |
    When I post the following form parameters to "/account":
      | name                 | value                 |
      | company              | Sparqlr               |
      | lastName             | Coates                |
      | firstName            | Phil                  |
      | email                | test.user@sparqlr.com |
      | user.password        | password              |
      | user.passwordConfirm | password              |
    Then I should get a 200 response code
    And the response body should contain the string "This email is already in use"