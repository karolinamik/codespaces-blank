Feature: Logger Data

  Scenario Outline: Send data for each logger type
    Given the logger with type "<logger_type>" is created
    When data is sent for created logger
    Then the system should save it

    Examples:
      | logger_type |
      | MR_810T     |
      | MR_812P     |
