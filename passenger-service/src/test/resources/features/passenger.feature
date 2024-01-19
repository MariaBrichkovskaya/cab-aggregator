Feature: Passenger Service

  Scenario: Retrieving a passenger by existing id
    Given A passenger with id 1 exists
    When The id 1 is passed to the findById method
    Then The response should contain passenger with id 1

  Scenario: Retrieving a passenger by non-existing id
    Given A passenger with id 1 doesn't exist
    When The id 1 is passed to the findById method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Deleting a passenger by non-existing id
    Given A passenger with id 1 doesn't exist
    When The id 1 is passed to the deleteById method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Deleting a passenger by existing id
    Given A passenger with id 1 exists
    When The id 1 is passed to the deleteById method
    Then The response should contain message with id 1

  Scenario: Creating a new passenger with non-unique phone
    Given A passenger with phone "80291234567" exists
    When A create request with email "maria@example.com", phone "80291234567" is passed to the add method
    Then The AlreadyExistsException should be thrown

  Scenario: Creating a new passenger with unique data
    Given A passenger with email "maria@example.com" and phone "80291234567" doesn't exist
    When A create request with email "maria@example.com", phone "80291234567" is passed to the add method
    Then The response should contain created passenger

  Scenario: Creating a new passenger with non-unique email
    Given A passenger with email "maria@example.com" exists
    When A create request with email "maria@example.com", phone "80291234567" is passed to the add method
    Then The AlreadyExistsException should be thrown

  Scenario: Update passenger by non-existing id
    Given A passenger with id 1 doesn't exist
    When An update request with email "maria@example.com", phone "80291234567" for passenger with id 1 is passed to the update method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Update passenger with unique data
    Given A passenger with id 1 exists when email "maria@example.com" and phone "80291234567" doesn't exist
    When An update request with email "maria@example.com", phone "80291234567" for passenger with id 1 is passed to the update method
    Then The response should contain updated passenger with id 1

  Scenario: Find all passengers
    Given A list of passengers
    When The findAll method is called with valid parameters
    Then A list of passengers is returned

  Scenario: Find all passenger with invalid parameters
    Given A list of passengers
    When The findAll method is called with invalid page
    Then The InvalidRequestException should be thrown for invalid page