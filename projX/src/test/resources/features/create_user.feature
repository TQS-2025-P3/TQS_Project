Feature: User creation with cars

  Scenario: Creating a user with multiple cars
    Given a user named "Joana" with email "joana@example.com" and password "admin123"
    And the user owns the following cars:
      | brand  | model    | rangeKm | ownerId |
      | Tesla  | Model Y  | 400   | 1       |
      | Nissan | Leaf     | 270   | 1       |
    When the user is saved
    Then the user should be created with name "Joana" and email "joana@example.com"
