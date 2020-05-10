package org.junit.runner;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.internal.TextListener;
import org.junit.runner.notification.Failure;

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

    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

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
            System.out.println("\t - Running test: " + description.getClassName() + "." + description.getMethodName());
        }

        @Override
        public void testFailure(Failure failure) {
            // Make sure that the hideous E is not printed anynmore
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("");
        System.out.println("Starting test executions of PUBLIC TESTS: " + dtf.format(LocalDateTime.now()));
        System.out.println("");

        /*
         * This method re-implements JunitCore.main() but replace the hideous default
         * TextListener with out that logs which test starts. To make it possible
         * without too much hassle I declare the same package as in JUnitCore
         */
        JUnitCore junit = new JUnitCore();

        Request request = null;

        if (args.length == 1 && args[0].contains("#")) {
            String className = args[0].split("#")[0];
            String methodName = args[0].split("#")[1];
            request = Request.method(Class.forName(className), methodName);
        } else {
            JUnitCommandLineParseResult jUnitCommandLineParseResult = JUnitCommandLineParseResult.parse(args);
            request = jUnitCommandLineParseResult.createRequest(JUnitCore.defaultComputer());
        }

        junit.addListener(new SuppressingOutputTextListener(System.out));

        Result result = junit.run(request);

        System.exit(result.wasSuccessful() ? 0 : 1);

    }

}
