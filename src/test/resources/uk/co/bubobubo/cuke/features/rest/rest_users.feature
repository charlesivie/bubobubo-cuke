Feature: rest operations for user

  Background: clean up before run
#    Given not yet implemented

  Scenario: create
    Given I post "json/test_user.json" to sparqlr "/rest/users" with
      | type   | name         | value            |
      | header | Accept       | application/json |
      | header | Content-Type | application/json |
    Then I should get a 200 response code
    And the response json path "id" should match "10"
    And the response json path "username" should match "charlie@whatever.com"
    And the response json path "password" should match "password"
    And the response json path "passwordConfirm" should match "password"

  Scenario: update
    Given I put "json/test_user.json" to sparqlr "/rest/users" with
      | type   | name         | value            |
      | header | Accept       | application/json |
      | header | Content-Type | application/json |
    Then I should get a 200 response code
    And the response json path "id" should match "10"
    And the response json path "username" should match "charlie@whatever.com"
    And the response json path "password" should match "password"
    And the response json path "passwordConfirm" should match "password"

  Scenario: delete
    Given I delete "/rest/users/10" from sparqlr
    Then I should get a 200 response code

  Scenario: list
    Given I get "/rest/users" from sparqlr
    Then I should get a 200 response code
    And the response json path "id[0]" should match "11"
    And the response json path "username[0]" should match "charlie@whatever1else.com"
    And the response json path "password[0]" should match "password1"
    And the response json path "passwordConfirm[0]" should match "password1"

  Scenario: get
    Given I get "/rest/users/10" from sparqlr
    Then I should get a 200 response code
    And the response json path "id" should match "10"
    And the response json path "username" should match "charlie@whatever.com"
    And the response json path "password" should match "password"
    And the response json path "passwordConfirm" should match "password"

  Scenario: update fails if username not valid email address

  Scenario: create fails if username not valid email address

  Scenario: update fails if id does not exists

  Scenario: create fails if id does exist

  Scenario: update fails if password does not match passwordConfirm

  Scenario: create fails if password does not match passwordConfirm

