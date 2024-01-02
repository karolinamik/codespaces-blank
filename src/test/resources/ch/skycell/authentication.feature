Feature: Authentication

  Scenario: API Authentication
    Given credentials are definied
    When sending a request to authenticate
    Then the response contains authentication token 
    And the token is a valid JWT token
