import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.hamcrest.MatcherAssert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Implements unit tests against test files (*_input.txt and corresponding
 * *_output.txt) in the directory specified by the -Dtest.data variable.
 * 
 * @author Fabian Schliski
 *
 */
@RunWith(Parameterized.class)
public class FileTest {

    String testName;
    private String[] input;
    private String expectedOutput;

    public FileTest(String testName, String[] input, String expectedOutput) {
        this.testName = testName;
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    @Parameters
    public static Collection<Object[]> data() {
        // Getting test.data property
        final String testDataLocation = System.getProperty("test.data");
        if (testDataLocation == null || testDataLocation.length() == 0) {
            throw new IllegalArgumentException("Runtime variable test.data not set.");
        }

        // Checking the folder
        final File testDataFolder = new File(testDataLocation);
        if (!testDataFolder.isDirectory() || !testDataFolder.exists()) {
            throw new IllegalArgumentException(
                    "Test data folder (" + testDataFolder.getAbsolutePath() + testDataLocation + ") is not a folder.");
        }

        // Reading test directory contents
        Map<String, String[]> inputs = new HashMap<>();
        Map<String, String> outputs = new HashMap<>();
        for (final File file : testDataFolder.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getAbsolutePath().endsWith(".txt");
            }
        })) {
            String fileName = file.getName();
            if (fileName.contains("fixed")) {
            	continue;
            }

            // Removing the _input.txt and _output.txt suffixes to get the test name
            String testName = fileName.replace("_input.txt", "").replace("_output.txt", "");

            // Reading the file contents
            String fileContent = readFile(file.getAbsolutePath());

            if (fileName.endsWith("_input.txt")) {
                inputs.put(testName, Stream.of(fileContent.split(" ")).map(s -> s.trim()).toArray(String[]::new));
            } else if (fileName.endsWith("_output.txt")) {
                outputs.put(testName, fileContent);
            }
        }

        List<Object[]> tests = new ArrayList<>();

        for (Map.Entry entry : inputs.entrySet()) {
            if (!outputs.containsKey(entry.getKey())) {
                throw new NullPointerException("Missing output test file for input test: " + entry.getKey());
            }
            Object[] test = { entry.getKey(), entry.getValue(), outputs.get(entry.getKey()) };
            tests.add(test);
        }
        return tests;
    }

    private static String readFile(String fileName) {
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.join(System.lineSeparator(), lines) + System.lineSeparator();
    }

    @BeforeClass
    public static void checkPrecondition() {
        /*
         * Check only once that all the required variables and configurations are
         * correctly set
         */
        PSTestUtils.validateTheExecutionEnvironment();
    }

    @Test(timeout = 3000)
    public void testInputOutputFile() throws Exception {
        // Execution
        Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithArgs(this.input);

        int exitCode = (Integer) result.get("exitCode");
        String stdOut = (String) result.get("stdOut");
        String stdError = (String) result.get("stdError");

        // Assertions

        // Does the program output match the expected one?
        String[] outputAsLines = stdOut.split("\\R");
        String[] expectedAsLines = this.expectedOutput.split("\\R");
        MatcherAssert.assertThat("The GUI output for test " + this.testName + " is not as expected.", outputAsLines,
                BoardMatcher.matchesBoard(expectedAsLines));
    }
}
