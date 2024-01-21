Feature: Driver Service

  Scenario: Retrieving a ride by existing id
    Given A ride with id 1 exists
    When The id 1 is passed to the findById method
    Then The response should contain ride with id 1

  Scenario: Retrieving a ride by non-existing id
    Given A ride with id 1 doesn't exist
    When The id 1 is passed to the findById method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Deleting a ride by non-existing id
    Given A ride with id 1 doesn't exist
    When The id 1 is passed to the deleteById method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Deleting a ride by existing id
    Given A ride with id 1 exists
    When The id 1 is passed to the deleteById method
    Then The response should contain message with id 1

  Scenario: Creating a new ride
    Given Create ride with default data
    When A create request is passed to the add method
    Then The response should contain created ride

  Scenario: Update ride by non-existing id
    Given A ride with id 1 doesn't exist
    When An update request for ride with id 1 is passed to the update method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Update driver
    Given A ride with id 1 for update exists
    When An update request for ride with id 1 is passed to the update method
    Then The response should contain updated ride with id 1

  Scenario: Change ride status by non-existing id
    Given A ride with id 1 doesn't exist
    When Ride id 1 is passed to the changeStatus method
    Then The NotFoundException with id 1 should be thrown

  Scenario: Change ride status by existing id
    Given A ride with id 1 for editing status exists
    When Ride id 1 is passed to the changeStatus method
    Then The response should contain updated ride with id 1

  Scenario: Change ride status by existing id when ride already finished
    Given A ride with id 1 for editing status exists when status finished
    When Ride id 1 is passed to the changeStatus method
    Then The AlreadyFinishedRideException should be thrown

  Scenario: Find all rides
    Given A list of rides
    When The findAll method is called with valid parameters
    Then A list of rides is returned

  Scenario: Find all rides with invalid parameters
    Given A list of rides
    When The findAll method is called with invalid page
    Then The InvalidRequestException should be thrown for invalid page

  Scenario: Passenger's rides history
    Given History for passenger with id 1
    When The getPassengerRides method is called for passenger with id 1
    Then Passenger's history is returned

  Scenario: Driver's rides history
    Given History for driver with id 1
    When The getDriverRides method is called for driver with id 1
    Then Driver's history is returned