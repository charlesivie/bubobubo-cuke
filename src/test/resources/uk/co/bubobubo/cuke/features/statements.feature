Feature: statements endpoint

  Background: setup
	Given bubobubo and sparqlr are running
	And I delete the test user and repo
	And I end the http session

  Scenario: signup create repo and perform statements query
	Given I start the http session
	And I create the test user and repo
	When I get "/repositories/test-repo-1/statements" with
	  | type   | name   | value               |
	  | header | accept | application/rdf+xml |
	Then I should get a 200 response code
	And the response body should match the file "rdf/statements.xml"

  Scenario: signup create repo and perform statements context query
	Given I start the http session
	And I create the test user and repo
	When I get "/repositories/test-repo-1/statements" with
	  | type      | name    | value               |
	  | header    | accept  | application/rdf+xml |
	  | parameter | context | _:n1234x5678        |
	Then I should get a 200 response code
	And the response body should match the file "rdf/statements_context.xml"

  Scenario: signup create repo and perform statements post query
	Given I start the http session
	And I create the test user and repo
	When I post "rdf/music.xml" to "/repositories/test-repo-1/statements" with
	  | type   | name         | value                             |
	  | header | Content-Type | application/rdf+xml;charset=UTF-8 |
	Then I should get a 204 response code

  Scenario: signup create repo and perform statements post update sparql 1.1 query
	Given I start the http session
	And I create the test user and repo
	When I post "sparql/update.txt" to "/repositories/test-repo-1/statements" with
	  | type   | name         | value                             |
	  | header | Content-Type | application/x-www-form-urlencoded |
	Then I should get a 204 response code

  Scenario: signup create repo and perform statements post update sparql 1.1 query with some actual data
	Given I start the http session
	And I create the test user and repo
	When I post "sparql/insert_something.txt" to "/repositories/test-repo-1/statements" with
	  | type   | name         | value                             |
	  | header | Content-Type | application/x-www-form-urlencoded |
	Then I should get a 204 response code

  Scenario: signup create repo and perform statements delete query
	Given I start the http session
	And I create the test user and repo
	When I delete "/repositories/test-repo-1/statements"
	Then I should get a 204 response code

  Scenario: I clean up