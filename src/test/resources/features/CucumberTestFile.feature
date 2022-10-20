Feature: Manage survey for a company

  Scenario Outline: Client makes call to create survey
    When the client sends input post call
    Then the client receives response
    And the client receives information about the surveyID with <statusCode>

    Examples:
      | statusCode |
      | 200     |


  Scenario Outline: Client makes call to submit survey response
    When the client sends input post call with valid data
    And the client receives information about the successful submission details with  <statusCode>

    Examples:
      | statusCode |
      | 201     |

  Scenario Outline: Client makes call to create survey
    When the client sends input post call with invalid data
    Then the client receives error response
    And the client receives information about the error details "<errorDescription>"

    Examples:
      | errorDescription |
      | Question Order number is not unique     |