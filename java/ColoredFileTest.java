import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Triple;
import org.hamcrest.MatcherAssert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * This test is based on {@link FileTest} by Fabian Schliski. It assumes the
 * availability of two additional files that encode the expectations about
 * foreground and background color of all the output chars. It works as follows:
 * feed the input file to the program after enabling the colors aspect and
 * capture the corresponding output. Then it "demultiplexes" the output into
 * three parts/files:
 * <ul>
 * <li>the UI, which contains only the regular chars. So the output with colors
 * striped away</li>
 * <li>the Background color, which contains for each "char" an id that
 * identifies the actual background color of that char</li>
 * <li>the Foreground color, which contains for each "char" an id that
 * identifies the actual foreground color of that char</li>
 * </ul>
 * <p>
 * Those parts/files are compared against the expected test files (*_output.txt,
 * *_output_background.txt, *_output_foreground.txt)
 * </p>
 * 
 * @author gambi
 *
 */
@RunWith(Parameterized.class)
public class ColoredFileTest {

    String testName;
    private String[] input;
    private String expectedOutputUI;
    private String expectedOutputBackground;
    private String expectedOutputForeground;

    public ColoredFileTest(String testName, String[] input, //
            String expectedOutput, String expectedOutputBackground, String expectedOutputForeground) {
        this.testName = testName;
        this.input = input;
        this.expectedOutputUI = expectedOutput;
        this.expectedOutputBackground = expectedOutputBackground;
        this.expectedOutputForeground = expectedOutputForeground;
    }

    @Parameters
    public static Collection<Object[]> data() {
        // Getting test.data property
        // TODO Replace this with an Assume condition
        final String testDataLocation = System.getProperty("test.data");
        if (testDataLocation == null || testDataLocation.length() == 0) {
            throw new IllegalArgumentException("Runtime variable test.data not set.");
        }

        // Checking the folder
        // TODO Replace this with an Assume condition
        final File testDataFolder = new File(testDataLocation);
        if (!testDataFolder.isDirectory() || !testDataFolder.exists()) {
            throw new IllegalArgumentException(
                    "Test data folder (" + testDataFolder.getAbsolutePath() + ") is not a folder.");
        }

        // Reading test directory contents
        Map<String, String[]> inputs = new HashMap<>();
        Map<String, String> outputUIs = new HashMap<>();
        Map<String, String> outputBackgrounds = new HashMap<>();
        Map<String, String> outputForegrounds = new HashMap<>();

        for (final File file : testDataFolder.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getAbsolutePath().endsWith(".txt");
            }
        })) {
            String fileName = file.getName();

            // Removing the suffixes to get the test name
            String testName = fileName.replace("_input.txt", "").replace("_output.txt", "")
                    .replace("_output_background.txt", "").replace("_output_foreground.txt", "");

            // Reading the file contents
            String fileContent = readFile(file.getAbsolutePath());

            if (fileName.endsWith("_input.txt")) {
                inputs.put(testName, Stream.of(fileContent.split(" ")).map(s -> s.trim()).toArray(String[]::new));
            } else if (fileName.endsWith("_output.txt")) {
                outputUIs.put(testName, fileContent);
            } else if (fileName.endsWith("_output_background.txt")) {
                outputBackgrounds.put(testName, fileContent);
            } else if (fileName.endsWith("_output_foreground.txt")) {
                outputForegrounds.put(testName, fileContent);
            }
        }

        List<Object[]> tests = new ArrayList<>();

        // TODO Replace with Assume
        for (Map.Entry entry : inputs.entrySet()) {
            if (!outputUIs.containsKey(entry.getKey())) {
                throw new NullPointerException(
                        "Missing file from outputUIs test files for input test: " + entry.getKey());
            }
            if (!outputBackgrounds.containsKey(entry.getKey())) {
                throw new NullPointerException(
                        "Missing file from outputBackgrounds test files for input test: " + entry.getKey());
            }
            if (!outputForegrounds.containsKey(entry.getKey())) {
                throw new NullPointerException(
                        "Missing file from outputForegrounds test files for input test: " + entry.getKey());
            }
            Object[] test = { entry.getKey(), entry.getValue(), //
                    outputUIs.get(entry.getKey()), outputBackgrounds.get(entry.getKey()),
                    outputForegrounds.get(entry.getKey()) };
            tests.add(test);
        }
        return tests;
    }

    private static String readFile(String fileName) {
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("ColoredFileTest.readFile() ERROR WHILE READING " + fileName);
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
        try {
            // Execution. Note that PreysAndHunters.ENABLE_COLORS is not visible here with
            // make !
            Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithFlagsAndArgs(
                    Arrays.asList(new String[] { "enable.colors" }), this.input);

            int exitCode = (Integer) result.get("exitCode");
            String stdOut = (String) result.get("stdOut");
            String stdError = (String) result.get("stdError");

            // Process the stdout to obtains the UI, Background and Foreground information.

            // Compute the background and foreground color map for the entire output
            Triple<String, String, String> coloredOutput = new ColorFSM().demultiplex(stdOut);
            String ui = coloredOutput.getLeft();
            String background = coloredOutput.getMiddle();
            String foreground = coloredOutput.getRight();

            // Does the program output match the expected one?
            String[] outputAsLines = ui.split("\\R");
            String[] expectedAsLines = this.expectedOutputUI.split("\\R");
            MatcherAssert.assertThat("The GUI output for test " + this.testName + " is not as expected.", outputAsLines,
                    BoardMatcher.matchesBoard(expectedAsLines));

            // Does the program background match the expected one?
            String[] backgroundAsLines = background.split("\\R");
            String[] expectedBackgroundAsLines = this.expectedOutputBackground.split("\\R");
            MatcherAssert.assertThat("The Background GUI output for test " + this.testName + " is not as expected.",
                    backgroundAsLines, BoardMatcher.matchesBoard(expectedBackgroundAsLines));

            // Does the program foreground match the expected one? Note that we ignore blank
            // chars here
            String[] foregroundAsLines = foreground.split("\\R");
            String[] expectedForegroundAsLines = this.expectedOutputForeground.split("\\R");
            MatcherAssert.assertThat("The Foreground GUI output for test " + this.testName + " is not as expected.",
                    foregroundAsLines, BoardMatcher.matchesBoardWithWildcardChar(expectedForegroundAsLines, ' '));
        } catch (Exception e) {
            System.err.println("ERROR FOR TEST " + this.testName);
            throw e;
        }

    }
}
