# Security Feed API

This is an application that reads the National Vulnerability Database feeds for new vulnerabilities and makes that data
available over an HTTP API. The LOG.md file has more details about what the project is, what it's goals are, and what
the underlying development ideas are

## Getting Started

In order to build this project locally, you will need access to Maven and a JDK that is at least version 11

Once the project is checked out, you can start the application by either running com.astarbia.securityapi.Application in
an IDE or by executing the following command from the security-feed-api directory

`mvn spring-boot:run`

## Running Unit Tests

In order to execute the unit tests, you can either open the project in an IDE and use the IDEs tools to run the suites,
or you can invoke the following command from the security-feed-api directory

`mvn test`

## Running E2E Tests

In order to execute the E2E tests of the software, you can either open the project in an IDE and use the IDEs tools to
run the suites, or you can invoke the following command on the command line from the e2e-test directory

Before these tests can run, you will need to ensure that the main application is running at the URL identified in the
properties file

`mvn verify`
