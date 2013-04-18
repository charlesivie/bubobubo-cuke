Feature: query for sparql responses

  Scenario: perform get request
    Given bubobubo is running
    And I create the test user
    And I create the test repo
    When I get "/repositories/conn-test-1?query=select * where { ?s ?p ?o }"
    Then I should get a 200 response code
