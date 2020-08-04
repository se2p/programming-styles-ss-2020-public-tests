package org.junit.runner;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.experimental.categories.Category;
import org.junit.internal.TextListener;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;

/**
 * A simple wrapper around JUnitCore to easily configure it. This also supports
 * categories expressed from the command line using
 * PSTestRunner.INCLUDE_CATEGORY
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

    public final static String INCLUDE_CATEGORY = "pah.assignment";

    public static void main(String[] args) throws ClassNotFoundException {

        String categoryName = System.getProperties().getProperty(INCLUDE_CATEGORY);

        System.out.println("");
        System.out.print("Starting test executions of PUBLIC TESTS: " + dtf.format(LocalDateTime.now()));
        if (categoryName != null && categoryName.trim().length() > 0) {
            System.out.println(" for " + categoryName);
        } else {
            System.out.println("");
        }
        System.out.println("");

        /*
         * This method re-implements JunitCore.main() but replace the hideous default
         * TextListener with out that logs which test starts. To make it possible
         * without too much hassle I declare the same package as in JUnitCore
         */
        JUnitCore junit = new JUnitCore();

        Request request = null;

        if (args.length == 1 && args[0].contains("#")) {
            String testClassName = args[0].split("#")[0];
            String testMethodName = args[0].split("#")[1];
            Class testClass = Class.forName(testClassName);
            request = Request.method(testClass, testMethodName);

        } else {
            JUnitCommandLineParseResult jUnitCommandLineParseResult = JUnitCommandLineParseResult.parse(args);
            request = jUnitCommandLineParseResult.createRequest(JUnitCore.defaultComputer());
        }

        // If a testClass is annotated with Category, then annotation must match. If
        // there's no annotation, we keep the test no matter what.

        if (categoryName != null && categoryName.trim().length() > 0) {
            final Class categoryClass;

            // TODO At the moment I cannot find anything better.
            try {
                // Try to load the class corresponding to the annotation
                categoryClass = Class.forName(categoryName);
            } catch (Throwable e) {
                throw new RuntimeException("Category not found " + categoryName, e);
            }

            /*
             * Replace the original request with one that contains only the test cases that
             * match the category
             */
            request = request.filterWith(new Filter() {

                @Override
                public boolean shouldRun(Description description) {
                    for (Annotation annotation : description.getAnnotations()) {
                        if (annotation.annotationType().equals(Category.class)) {
                            Class<? extends Annotation> type = annotation.annotationType();
                            for (Method method : type.getDeclaredMethods()) {
                                // We know Category returns Class<?>[]
                                Class[] value;
                                try {
                                    value = (Class[]) method.invoke(annotation, (Object[]) null);
                                    for (Class c : value) {
                                        if (c.equals(categoryClass)) {
                                            // Found the match.
                                            return true;
                                        }
                                    }
                                } catch (IllegalAccessException | IllegalArgumentException
                                        | InvocationTargetException e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            }

                            return false;
                        }
                    }
                    return true;
                }

                @Override
                public String describe() {
                    return "Filter by annotation";
                }
            });

        }

        junit.addListener(new SuppressingOutputTextListener(System.out));

        Result result = junit.run(request);

        System.exit(result.wasSuccessful() ? 0 : 1);

    }

}
