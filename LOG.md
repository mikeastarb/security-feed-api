# Security Feed API Log

This log is a stream of ideas, thoughts, and implementation notes for this project

## Context (Copied)

Note: For the purposes of this project, the UI is not going to be necessary to code

As part of this project, we would like for you to build a simple, yet elegant application that visualizes a security intelligence data feed (vulnerabilities or threat intelligence data). We ask that your UI contains at least:

    Map if you are visualizing geo-location data that can zoom in/out
    Grid control (aka...a table of data) to present the data presented in the map
    Search of data to change the presentation in the map and grid.

It should be a clean UI written in one of the recommended JS frameworks below. The service layer should be written in Java.

Feel free to use any of the these JavaScript frameworks:

    Angular
    React
    Vue
    Meteor
    Ember

We recommend that you identify one or more data feeds from one of these three sources.

    Interesting GitHub Project with Curated Threat Intelligence Feeds
    National Vulnerability Database
    Choose or recommend an alternate data set.

Additional requirements as part of our craftsmanship initiative:

    Make sure to write an amazing README in your GitHub project that explains what your built, why you built it, how to set it up and how to use it.
    Unit tests
    Integration tests of your service layer
    CI Pipeline to compile, build, test and report in Travis CI which is free for any Open Source project in GitHub

## Initial Architectural Thoughts

Looking at the UI requirements of what the API layer may need to support, there are a few considerations for a data format. First, the format will need to be able to identify a type
of data, be it a security incident report or a new vulnerability report. These items will need to also have a description of what the item is, a reported date/time, and a source ID where possible. Lastly, some of these items may be tagged with geo-location data, and will need to have fields for that as well

As far as architectural consideration goes, I'm thinking that there will be a common, internal data model that will be used by this system. This would be used when doing any kind of
server-side filtering, paging, searching etc based on the values we support. Each data source could then be coded in with a gatherer component that manages reading the feed at given intervals, and an adapter component that handles translation between the source data format and our own

## Project Approach

At a high level, this project has a few goals; primarily it's a demonstration of my own skill and expertise with software development and practices across the SDLC including setting up things like CI/CD pipelines with Travis, Dockerizing the final build, etc. With that in mind, my initial implementation will be choosing the NVD feed as an initial source and will work that through to completion. Before we can get to that point though, the plumbing needs to be setup, and then a skeleton needs to be built

Below are the overall steps I plan to take to get to a point where I can begin iteration on features:

* Get initial thoughts developed in this LOG
* Begin a README that details the top file/folder structures found in this repo (keep this updating all the time)
* Create a skeleton/empty SpringBoot Java project that can compile, run, and produce some form of HTTP output that can be verified (healthcheck)
* Create the foundation of an E2E test suite that attempts to connect to a running version of the app and verify that the health check is correct
  * E2E tests for now will be configured to run against localhost, but should be extendable out to running against deployed environments, like a QA location
* Create the foundation of a Unit test suite that runs a simple test that can be configured to pass or fail at runtime (reason below)
* Create a build pipeline for Travis CI that fails if any Unit or E2E tests fail (test this by giving it a configuration that will fail the unit test)

Next, I'll take a TDD/ATDD approach to building out what would be needed for my initial implementation

* Create an E2E test for reading a feed from a local file in our own format. This gets the heart of the ETL system going in a simple case
  * In order to really test that the file is being read; have the test generate the file
  * Also need a test that looks at how a malformatted file is handled - Healthcheck should be able to make notes of incoming feed statuses
* As work begins to implement the controller, model, services, etc. for the application to actually do the work, start on a unit test suite
  * For unit testing, I'll aim for high coverage, but will be culling out tests that are not verifying anything of importance
* Grow this into developing tests/components for accessing NVD directly and displaying the latest of their smallest data set
* Add a feature so that results are held in memory for 12 hours and then be removed. E2E tests may not be able to really verify the 12 hour time, but a Unit Test would
* Add a feature for pagination in the results, to specify a page size and a page number - This would be useful for batching back-end calls and splitting up processing
* Filter the incidents for date/time ranges
* Simple text search for incident descriptions that contain a value
* Sorting incidents by ID, date/time, description

## Additional Features to Consider but Not Implement

* Some form of NLP to read a description and pull out a component name and version from a description. This would take time to implement, but would be useful for doing BOM matching similar to Black Duck and other tools
* Modify the data source abstraction layer so it could be configured by the environment for different feeds and processing. These feeds have a lot of commonality in how they are read and how they're generally parsed. If we could provide a mapping for a given feed from their data format to ours, the URL of that feed with any authentication/authorization information, and interval that the feed refreshes, we could put that into a configuration file for each feed, then have the system create multiple parsers on boot (or even better hot swap configurations as they're added, removed, edited without need for a reboot)
* Functional Testing - Once this application was deployed into a real environment, we would want to re-run the E2E tests against that deployed location as well as any other Functional or System Integration Tests that may be doing additional checks that the system is interacting with the correct environments, etc. Ideally this layer would be small, but would likely still exist
* Data Persistence Layer - For this initial implementation, I'll be keeping data in-memory only and culling information after it's been in the system for 12 hours to keep memory usage under semi-control. In a real application, we would be reading/storing information into a data persistence layer like MySQL/Postgres/Mongo or even right to a file system either locally or to something like S3. When considering this, we would want to consider how much information is kept in memory on the system for speed and how much we would flush to and read from disk to maintain our own log of information

# Development Log

* Setup is starting to get underway with the e2e suite getting logging handled and tests running, but currently failing. The basic structure is something I'm pulling from my experience writing these kinds of suites in my current job
* Got a simple springboot app running with actuators so test should be passing, but it's not
* Refactored the project to reduce the complexity of setup. The overall cost is that the e2e suite will not be able to run as a regular Spring integration test and a test against a deployed environment without further configuration. This is always something that can be added later
* Getting things setup with Travis for builds/tests on push to the repo. Once here, we can start adding the core features in
* Need to rethink a bit about how to approach things from here since there won't be opportunity to inject a file into the system
  * Thinking about first setting up static data to be returned; that tests the return pipeline
  * Then we can add an injection end-point that, in a PROD system, would ideally be locked out to non-admins but in practice would probably be locked by a configuration. There could be a valid use case for having something so the admins can inject items into the system in PROD; trying not to write/design system features just for testing
  * Once we can inject, we write tests to verify the injection, which is done by verifying the read