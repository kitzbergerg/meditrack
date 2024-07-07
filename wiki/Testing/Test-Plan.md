The test plan is designed to establish the testing strategy for MediTrack, ensuring that it is error-free and meets the defined requirements. The main goal of the tests is to ensure the quality and reliability of the web application by applying various testing stages.

# Test Scope

## Unit Tests

Unit tests will be conducted at the lowest level, involving the verification of individual methods or classes of the system. This is done in parallel with the implementation of individual methods and classes.

## Component Tests
Component tests will focus on validating complete components, which consist of multiple classes or modules, to ensure they function correctly within the system.

## Integration Tests

Integration tests are performed to ensure the collaboration of multiple interdependent components. These tests are conducted after the completion of integration implementations and successful unit tests.

## System Test

The system test is conducted to ensure that the entire system meets the specified requirements. This test is manually performed in the final phase of development.

# Test Responsibilities

## Development Phase

During the development phase, developers are responsible for conducting unit tests and integration tests. They must ensure that their code changes do not adversely affect existing functionality.

## Testing Phase

In the testing phase, the test manager is responsible for conducting the system test. This team reviews the entire system for compliance with requirements and reports potential issues to the rest of the team. The individual responsible for testing is primarily the test manager @11905145.

# Test Execution

## Unit Tests

Unit tests cover individual methods or classes and are automatically performed with each code change. Developers ensure that all tests pass successfully before merging changes into the main repository.

## Component Tests
Component tests will be executed in conjunction with integration tests, ensuring that individual components function correctly within the larger system context.

## Integration Tests

Integration tests are conducted as needed when relevant components are integrated to ensure the collaboration between different components.

## System Test

The system test is manually performed to ensure that the entire system meets the requirements.

# Test Documentation

Test cases from system tests are documented in the wiki under [Manual System Tests](https://reset.inso.tuwien.ac.at/repo/2024ss-ase-pr-group/24ss-ase-pr-qse-03/-/wikis/Testing/Manual-System-Tests). For each system test, the protocol, report, error analysis, and test result are documented under [Test Protocols and Reports](https://reset.inso.tuwien.ac.at/repo/2024ss-ase-pr-group/24ss-ase-pr-qse-03/-/wikis/Testing/Test-Protocols-and-Reports).

# Test Completion

This test plan serves as a guide for conducting tests during the development process of MediTrack. Adhering to this plan will help ensure the quality and reliability of the web application.