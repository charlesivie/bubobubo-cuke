Feature: query for sparql responses

  Background: setup
    Given bubobubo and sparqlr are running
    And I delete the test user and repo

  Scenario: perform unauthorised get request
    Given I start the http session
    And I create the test user and repo
    And I end the http session
    When I get "/repositories/test-repo-1" with parameters
      | name    | value                       |
      | query   | select * where { ?s ?p ?o } |
      | queryLn | sparql                      |
    Then I should get a 401 response code

  Scenario: signup create repo and query
    Given I start the http session
    And I create the test user and repo
    When I get "/repositories/test-repo-1" as test user with parameters
      | name    | value                       |
      | query   | select * where { ?s ?p ?o } |
      | queryLn | sparql                      |
    Then I should get a 200 response code
    And I end the http session
