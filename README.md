# Security Feed API

[![Build Status](https://github.com/mikeastarb/security-feed-api/actions/workflows/build.yml/badge.svg)](https://github.com/mikeastarb/security-feed-api/actions/workflows/build.yml)

This is an application that reads the National Vulnerability Database feeds for new vulnerabilities and makes that data
available over an HTTP API. The LOG.md file has more details about what the project is, what it's goals are, and what
the underlying development ideas are

## Getting Started

In order to build this project locally, you will need Maven and a JDK that is at least version 11

Once the project is checked out, you can start the application by either running com.astarbia.securityapi.Application in
an IDE or by executing the following command

`mvn spring-boot:run`

## Running Unit Tests

In order to execute the unit tests, you can either open the project in an IDE and use the IDEs tools to run the suites,
or you can invoke the following command

`mvn test`

## Running Unit and E2E Tests

In order to execute the Unit and E2E tests of the software, you can either open the project in an IDE and use the IDEs
tools to run the suites, or you can invoke the following command on the command line

`mvn verify`

## Building the Docker Image

In order to build the docker image locally, please execute the following from the project root

`docker build -t mikeastarb/security-feed-api .`

## Running the Built Docker Image

Once the docker image is built, you can run the image locally with a command similar to the following

`docker run --name security-feed -p 8080:8080 -d mikeastarb/security-feed-api`

# API Usage

The main functionality provided by this tool is the GET /incidents end-point which returns a set of information about
different security incidents and CVEs. When the application is running, information about this end-point can be found
in the swagger documentation at /swagger-ui.html