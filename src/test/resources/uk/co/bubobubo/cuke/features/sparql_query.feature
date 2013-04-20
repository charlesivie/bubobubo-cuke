Feature: query for sparql responses

  Background: setup
    Given bubobubo and sparqlr are running
    And I delete the test user and repo

  Scenario: Cannot create user if passwords dont match
    Given Passwords do not match on create
    And I create the test user and repo

  Scenario: perform get request
    Given I start the http session
    And I create the test user and repo
    And I end the http session
    When I get "/repositories/test-repo-1" as test user with parameters
      | name  | value                       |
      | query | select * where { ?s ?p ?o } |
    Then I should get a 200 response code
