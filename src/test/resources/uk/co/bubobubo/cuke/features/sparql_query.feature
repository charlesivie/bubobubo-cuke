Feature: query for sparql responses

  Background: setup
	Given bubobubo and sparqlr are running
	And I delete the test user and repo

  Scenario: Cannot create user if passwords dont match
	Given Passwords do not match on create

  Scenario: perform get request
	Given I start the http session
	And I create the test user and repo
	When I get "/repositories/conn-test-1?query=select * where { ?s ?p ?o }" as test user
	Then I should get a 200 response code
	And I end the http session
