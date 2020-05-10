#!/bin/bash

#
# Specify the folder containing the Prey and Hunter javascript version of the game for the assignment.
# If you checkout the public test project as submodule for your assignment as suggested, the compiled game should
# be located under ../javascript/
#
PREYS_AND_HUNTERS_HOME=${PREYS_AND_HUNTERS_HOME:-"../javascript"}
TEST_DATA_DIR="../test_data"

if [ $# -lt 1 ]; then echo "Missing test name"; exit 1; fi

# To run the tests you must invoke node passing the pah_home variable from the command line:
# Be sure you have installed mocha !
npm --pah_home=${PREYS_AND_HUNTERS_HOME} --test_data=${TEST_DATA_DIR} test
