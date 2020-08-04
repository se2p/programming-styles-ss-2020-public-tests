# This is taken from: https://blog.jayway.com/2013/05/19/auto-install-node-js-and-npm-from-makefile/
.PHONY: test

TEST_DATA_DIR = ../test_data

default: test

# If mocha is not available, install it
./node_modules/.bin/mocha: 
	npm install mocha

# Tests can be run only if mocha is available
# https://github.com/strongloop/loopback-next/issues/2755
test: ./node_modules/.bin/mocha
	npm --pah_home=$(PREYS_AND_HUNTERS_HOME) --test_data=${TEST_DATA_DIR} test -- --grep=$(ASSIGNMENT)
