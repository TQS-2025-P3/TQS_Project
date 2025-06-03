@requirementKey=TQSPROJECT-468
Feature: User creation with cars

  Scenario: Creating a user with multiple cars
    Given a user named "Joana" with email "joana@example.com" and password "admin123"
    And the user owns the following cars:
      | brand  | model    | plate     | batteryCapacity | userId |
      | Tesla  | Model Y  | AA-00-TY  | 75              | 1      |
      | Nissan | Leaf     | BB-11-LF  | 40              | 1      |
    When the user is saved
    Then the user should be created with name "Joana" and email "joana@example.com"
