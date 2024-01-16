Feature: Driver Service

  Scenario: Retrieving a driver by existing id
    Given A driver with id 1 exists
    When The id 1 is passed to the findById method
    Then The response should contain driver with id 1

  Scenario: Retrieving a driver by non-existing id
    Given A driver with id 1 doesn't exist
    When The id 1 is passed to the findById method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Deleting a driver by non-existing id
    Given A driver with id 1 doesn't exist
    When The id 1 is passed to the deleteById method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Deleting a driver by existing id
    Given A driver with id 1 exists
    When The id 1 is passed to the deleteById method
    Then The response should contain message with id 1

  Scenario: Creating a new driver with non-unique phone
    Given A driver with phone "80291234567" exists
    When A create request with phone "80291234567" is passed to the add method
    Then The AlreadyExistsException should be thrown for phone "80291234567"

  Scenario: Creating a new driver with unique data
    Given A driver with phone "80291234567" doesn't exist
    When A create request with phone "80291234567" is passed to the add method
    Then The response should contain created driver

  Scenario: Update driver by non-existing id
    Given A driver with id 1 doesn't exist
    When An update request with phone "80291234567" for driver with id 1 is passed to the update method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Update driver with unique data
    Given A driver with id 1 exists when phone "80291234567" doesn't exist
    When An update request with phone "80291234567" for driver with id 1 is passed to the update method
    Then The response should contain updated driver with id 1

  Scenario: Change driver status by non-existing id
    Given A driver with id 1 doesn't exist
    When Driver id 1 is passed to the changeStatus method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Change driver status by existing id
    Given A driver with id 1 exists
    When Driver id 1 is passed to the changeStatus method
    Then The response should contain status message with id 1
