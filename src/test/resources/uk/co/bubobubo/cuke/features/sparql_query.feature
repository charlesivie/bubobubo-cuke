Feature: query for sparql responses

  Background: setup
    Given bubobubo and sparqlr are running
    And I delete the test user and repo
    And I end the http session

  Scenario: perform unauthorised get request
    Given I start the http session
    And I create the test user and repo
    And I end the http session
    When I get "/repositories/test-repo-1" as unauthorised with
      | type      | name    | value                       |
      | parameter | query   | select * where { ?s ?p ?o } |
    Then I should get a 401 response code

  Scenario: signup create repo and query through rest
    Given I start the http session
    And I create the test user and repo
    When I get "/repositories/test-repo-1" with
      | type      | name    | value                                     |
      | parameter | query   | select * where { ?s ?p ?o }               |
      | header    | Accept  | application/sparql-results+xml, */*;q=0.5 |
    Then I should get a 200 response code

  Scenario: signup create repo and query through rest without accept heder
    Given I start the http session
    And I create the test user and repo
    When I get "/repositories/test-repo-1" with
      | type      | name    | value                                     |
      | parameter | query   | select * where { ?s ?p ?o }               |
    Then I should get a 200 response code

  Scenario: signup create repo and perform ask query
    Given I start the http session
    And I create the test user and repo
    When I get "/repositories/test-repo-1" with
      | type      | name   | value                   |
      | parameter | query  | ask { ?s ?p ?o }        |
      | header    | Accept | text/boolean, */*;q=0.5 |
    And the response should be "true"

