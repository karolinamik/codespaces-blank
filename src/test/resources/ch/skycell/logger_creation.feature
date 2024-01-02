Feature: Logger Creation

  Scenario Outline: Create logger
    Given the API for logger creation is defined
    When a user makes API requests to create logger with type "<logger_type>"
    Then the system should respond with successful creation messages for each logger

    Examples:
      | logger_type |
      | MR_810T     |
      | MR_812P     |

  Scenario Outline: Logger with incorrect type not created
    Given "<logger_type>" or "<logger_number>" is invalid
    Then the system should not create the logger

    Examples:
      | logger_type | logger_number |
      | MR_810T     | ZVFDVBF543543 |
      | dsfdfdg     | ABCCBA12345   |