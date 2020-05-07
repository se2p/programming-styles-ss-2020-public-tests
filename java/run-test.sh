#!/bin/bash

#
# Specify the folder containing the Prey and Hunter game for the assignment.
# If you checkout the public test project as submodule for your assignment as suggested, the compiled game should
# be located under ../java/
#
PREYS_AND_HUNTERS_HOME="../java"

if [ $# -lt 1 ]; then echo "Missing test name"; exit 1; fi

# To run one test you must invoke JUnitCore from the command line and pass the Class name of the TestCase, e.g., BasicTests
java -cp .:./libs/junit-4.13.jar:./libs/hamcrest-2.2.jar -Dpah.home=${PREYS_AND_HUNTERS_HOME} org.junit.runner.JUnitCore  ${TEST_NAME}
