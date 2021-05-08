Feature: Create New Game
  Test creation of new games and validation of the parameters

  Scenario: game endpoint is called with POST
    When the game endpoint is called with POST with white player name "Magnus Carlsen" and black player name "Anna Rudolph"
    Then a 200 response should be returned
    And the game is returned

  Scenario Outline: game endpoint is called with bad inputs
    When the game endpoint is called with POST with white player name <white> and black player name <black>
    Then a 400 response should be returned
    Examples:
      | white            | black          |
      | "   "            | "Anna Rudolph" |
      | ""               | "Anna Rudolph" |
      | "Magnus Carlsen" | "     "        |
      | "Magnus Carlsen" | ""             |
      | ""               | ""             |
      | " "              | " "            |


