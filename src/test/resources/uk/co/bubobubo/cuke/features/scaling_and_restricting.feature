Feature: scaling and restricting repositories - small, medium and large

  Background: setup
	Given bubobubo is running
	And sparqlr is running
	And I delete the test user and repo
	And I end the http session

  Scenario: restrict the free repository to 1000
	Given I start the http session
	And I create the test user and repo
	When I post "rdf/music_999_triples.xml" to "/repositories/test-repo-1/statements" with
	  | type   | name         | value                             |
	  | header | Content-Type | application/rdf+xml;charset=UTF-8 |
	Then I should get a 204 response code
	And the number of explicit triples in from "test-repo-1" is 999
	When I post "rdf/music.xml" to "/repositories/test-repo-1/statements" with
	  | type   | name         | value                             |
	  | header | Content-Type | application/rdf+xml;charset=UTF-8 |
	Then I should get a 403 response code
	And the response body should contain the string "FREE repo's are limited to 1,000 triples. logon to Sparqlr.com to upgrade."

  Scenario: restrict the small repository to 10,000
	Given I start the http session
	And I create the test user and repo
	When I post "rdf/music_9999_triples.xml" to "/repositories/test-repo-1/statements" with
	  | type   | name         | value                             |
	  | header | Content-Type | application/rdf+xml;charset=UTF-8 |
	Then I should get a 204 response code
	When I post "rdf/music.xml" to "/repositories/test-repo-1/statements" with
	  | type   | name         | value                             |
	  | header | Content-Type | application/rdf+xml;charset=UTF-8 |
	Then I should get a 403 response code
	And the response body should contain the string "SMALL repo's are limited to 10,000 triples. logon to Sparqlr.com to upgrade."

  Scenario: restrict the medium repository to 100,000
	Given I start the http session
	And I create the test user and repo
	When I post "rdf/music_99999_triples.xml" to "/repositories/test-repo-1/statements" with
	  | type   | name         | value                             |
	  | header | Content-Type | application/rdf+xml;charset=UTF-8 |
	Then I should get a 204 response code
	When I post "rdf/music.xml" to "/repositories/test-repo-1/statements" with
	  | type   | name         | value                             |
	  | header | Content-Type | application/rdf+xml;charset=UTF-8 |
	Then I should get a 403 response code
	And the response body should contain the string "MEDIUM repo's are limited to 100,000 triples. logon to Sparqlr.com to upgrade."


  Scenario: restrict the large repository to 1,000,000
	Given I start the http session
	And I create the test user and repo
	When I post "rdf/music_999999_triples.xml" to "/repositories/test-repo-1/statements" with
	  | type   | name         | value                             |
	  | header | Content-Type | application/rdf+xml;charset=UTF-8 |
	Then I should get a 204 response code
	When I post "rdf/music.xml" to "/repositories/test-repo-1/statements" with
	  | type   | name         | value                             |
	  | header | Content-Type | application/rdf+xml;charset=UTF-8 |
	Then I should get a 403 response code
	And the response body should contain the string "LARGE repo's are limited to 1,000,000 triples. logon to Sparqlr.com to upgrade."


  Scenario: Scale a repository
	Given I start the http session
	And I create the test user and repo
	When I put to "/repository/test-repo-1/scale/small" on sparqlr
	Then I should get a 200 response code
	And the response body JSON should match the file "expected/scale/small.json"
	When I put to "/repository/test-repo-1/scale/medium" on sparqlr
	Then I should get a 200 response code
	And the response body JSON should match the file "expected/scale/medium.json"
	When I put to "/repository/test-repo-1/scale/large" on sparqlr
	Then I should get a 200 response code
	And the response body JSON should match the file "expected/scale/large.json"
	When I put to "/repository/test-repo-1/scale/free" on sparqlr
	Then I should get a 200 response code
	And the response body JSON should match the file "expected/scale/free.json"

  Scenario: I clean up