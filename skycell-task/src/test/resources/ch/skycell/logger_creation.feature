Feature: Logger Creation

  Scenario Outline: Create logger
    Given the API for logger creation is defined
    When a user makes API requests to create logger with type "<logger_type>"
    Then the system should respond with successful creation messages for each logger
    
  Examples:
    | logger_type |
    | MR_810T     |
    | MR_812P     |