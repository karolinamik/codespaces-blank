Feature: Logger Creation

  Scenario Outline: Create logger
    Given the API for logger creation is defined
    When a user makes API requests to create logger with type "<logger_type>"
    Then the system should respond with successful creation messages for each logger
    
  Examples:
    | logger_type |
    | MR_810T     |
    | MR_812P     |

  Scenario: Logger with incorrect type not created
    Given the incorrect logger type
    When a user makes API requests to create logger
    Then the system should not create the logger

  Scenario: Logger with incorrect number not created
    Given the incorrect logger number
    When a user makes API requests to create logger
    Then the system should not create the logger
