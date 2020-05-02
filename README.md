Programming Styles -- SoSe20
---

# Public Tests

This repository hosts the public tests for the assignments of the Programming Style, SS2020.

## Java Tests

Public tests for the `java` tasks are implemented using JUnit 4.13 and Hamcrest and are executed using `make test`. They cab be also executed using the command line (check `run-test.sh`).

All the tests are meant to be **system tests**, that is, they will test the functionality of the code without looking inside it. Instead, tests call PreysAndHunters `main` method proving a sequence of input arguments, capture the exit code of the program as well as its stdOut and stdErr. At this point, assertions can be defined over exitCode, stdOut and stdErr.

`BasicTest.java` is an example on how test cases can be organized.

Before starting with the actual tests, using `@BeforeClass`, it checks the preconditions on the execution environment (java version, existence of the PreysAndHunters compiled game, etc.). It uses `Assumptions` not `Assertions` to do so, hence the tests will not fail if assumptions are not met, instead they will be skipped.

After checking preconditions, the tests are executed.

The `testThatGivenCorrectInputsTheProgramExitNormally` test exemplifies how tests can be implemented by:

1. Defining the sequence of inputs to be passed to the program
2. Calling the  `PSTestUtils.executePreysAndHuntersWithArgs` to execute the program under tests in a separate process and capture exitCode, stdOut and stdErr
3. Making assertions on exitCode, stdOut and stdErr

## Javascript Tests
Public tests for the `javascript` tasks are implemented using Mocha and can be executed either using `make test` or the `run-test.sh`).

Tests must be placed under the `test` otherwise Mocha will not find them.

Javascript tests follow the same idea of java tests: they import the program as dependency of the test, define the arguments that must be passed to it, and  makes assertions.

> TODO: To capture log messages, check [here](https://glebbahmutov.com/blog/capture-all-the-logs/) and [here](https://medium.com/@the_teacher/how-to-test-console-output-console-log-console-warn-with-rtl-react-testing-library-and-jest-6df367736cf0)


