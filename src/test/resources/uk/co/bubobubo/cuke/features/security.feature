Feature: query for sparql responses as various users with varying access rights

  Background: setup
    Given bubobubo and sparqlr are running
    And I delete the test user and repo
    And I end the http session

  Scenario: user performs get request, should get 401
    Given I start the http session
    And I create the test user and repo
    And I end the http session
    When I get "/repositories/test-repo-1" as user with
      | type      | name  | value                       |
      | parameter | query | select * where { ?s ?p ?o } |
    Then I should get a 401 response code

  Scenario: perform unauthorised get request
    Given I start the http session
    And I create the test user and repo
    And I end the http session
    When I get "/repositories/test-repo-1" as unauthorised with
      | type      | name  | value                       |
      | parameter | query | select * where { ?s ?p ?o } |
    Then I should get a 401 response code

  Scenario: perform get request as created but incorrect repo user
    Given I start the http session
    And I create the test user and repo
    And I create test repo 2
    And I end the http session
    When I get "/repositories/test-repo-1" as repo 2 with
      | type      | name  | value                       |
      | parameter | query | select * where { ?s ?p ?o } |
    Then I should get a 403 response code
