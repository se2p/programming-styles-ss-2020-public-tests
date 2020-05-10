# This is taken from" https://www.cs.swarthmore.edu/~newhall/unixhelp/javamakefiles.html

#
# define compiler and compiler flag variables
#

JC = javac
JAVA = java

.SUFFIXES: .java .class


TESTS = $(shell find . -iname '*Test.java' -exec basename {} \; | sed 's|.java||g')
TEST_CLASSES = $(shell find . -iname '*Test.java')
CLASSES = $(shell find . -iname '*.java' -not -iname '*Test.java')

TESTING_CLASSPATH = .:./libs/junit-4.13.jar:./libs/hamcrest-2.2.jar
TEST_DATA_DIR = ../test_data

.java.class:
	$(JC) -cp $(TESTING_CLASSPATH) $*.java

default: compile

# Compile test classes
compile: $(TEST_CLASSES:.java=.class) $(CLASSES:.java=.class)

# This target assumes that the public test project was checkout as submodule under public-tests
test: $(TEST_CLASSES:.java=.class) $(CLASSES:.java=.class)
	$(JAVA) -cp $(TESTING_CLASSPATH) -Dfile.encoding=UTF-8 -Dpah.home=$(PREYS_AND_HUNTERS_HOME) -Dtest.data=${TEST_DATA_DIR} org.junit.runner.PSTestRunner $(TESTS)

# Allows to run a single test following the format TestCase#testMethod
single-test: $(TEST_CLASSES:.java=.class) $(CLASSES:.java=.class)
	$(JAVA) -cp $(TESTING_CLASSPATH) -Dpah.home=$(PREYS_AND_HUNTERS_HOME) -Dtest.data=${TEST_DATA_DIR} org.junit.runner.PSTestRunner $(TEST)

clean:
	$(shell find . -name '*.class' -exec rm {} \;)