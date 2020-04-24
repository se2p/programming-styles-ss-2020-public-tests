package org.junit.runner;

import java.io.PrintStream;

import org.junit.internal.TextListener;

/**
 * A simple wrapper around JUnitCore to easily configure it.
 * 
 * @see <a href=
 *      "https://www.logicbig.com/tutorials/unit-testing/junit/junit-core.html">
 *      https://www.logicbig.com/tutorials/unit-testing/junit/junit-core.html</a>
 * 
 * @see <a href=
 *      "https://stackoverflow.com/questions/41261889/is-there-a-way-to-disable-org-junit-runner-junitcores-stdout-output">
 *      https://stackoverflow.com/questions/41261889/is-there-a-way-to-disable-org-junit-runner-junitcores-stdout-output</a>
 * 
 * @author gambi
 *
 */
public class PSTestRunner {

    static final class SuppressingOutputTextListener extends TextListener {

        public SuppressingOutputTextListener(PrintStream writer) {
            super(writer);
        }

        @Override
        public void testAssumptionFailure(org.junit.runner.notification.Failure failure) {
            System.out.println(
                    "Assumption not met for " + failure.getDescription().getClassName() + ": " + failure.getMessage());
        }

        @Override
        public void testStarted(Description description) {
            System.out.println("Running test: " + description.getMethodName());
        }
    }

    public static void main(String[] args) {
        /*
         * This method re-implements JunitCore.main() but replace the hideous default
         * TextListener with out that logs which test starts. To make it possible
         * without too much hassle I declare the same package as in JUnitCore
         */
        JUnitCore junit = new JUnitCore();
        // Expose
        JUnitCommandLineParseResult jUnitCommandLineParseResult = JUnitCommandLineParseResult.parse(args);

        junit.addListener(new SuppressingOutputTextListener(System.out));

        Result result = junit.run(jUnitCommandLineParseResult.createRequest(JUnitCore.defaultComputer()));
        System.exit(result.wasSuccessful() ? 0 : 1);

    }

}
