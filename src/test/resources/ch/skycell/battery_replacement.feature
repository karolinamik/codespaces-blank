Feature: Battery replacement

  Scenario: Retrieve a list of locations with number of batteries below threshold
    Given the user is authenticated on the cloud platform
    When user requests locations with numbers of loggers below the battery threshold
    Then the cloud platform responds with a list
    And list is ordered by the number of batteries below the threshold

