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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.ArrayMatching;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import aspects.TestGameStats;

/**
 * 
 * This is a mere combination of {@link ColoredFileTest} and
 * {@link GameStatsFileTest}.
 * 
 * @author gambi
 *
 */
@Category({ Assignment3.class })
@RunWith(Parameterized.class)
public class CombinedAspectFileTest {

    String testName;
    private String[] input;
    // to check colors
    private String expectedOutputUI;
    private String expectedOutputBackground;
    private String expectedOutputForeground;
    // to check game stats
    private String expectedGameStats;

    public CombinedAspectFileTest(String testName, String[] input, //
            String expectedOutputUI, //
            String expectedOutputBackground, String expectedOutputForeground, //
            String expectedGameStats) {
        this.testName = testName;
        this.input = input;
        this.expectedOutputUI = expectedOutputUI;
        this.expectedOutputBackground = expectedOutputBackground;
        this.expectedOutputForeground = expectedOutputForeground;
        this.expectedGameStats = expectedGameStats;
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
        Map<String, String> gameStats = new HashMap<>();

        List<File> testDataFiles = Arrays.asList(testDataFolder.listFiles(new FileFilter() {

            // We can about input and output files only but not "fixed"
            @Override
            public boolean accept(File pathname) {
                boolean isFixedOutput = pathname.getAbsolutePath().contains("fixed");
                boolean isInput = pathname.getAbsolutePath().endsWith("_input.txt");
                boolean isOutput = pathname.getAbsolutePath().endsWith("_output.txt");
                boolean isOutputBackground = pathname.getAbsolutePath().endsWith("_output_background.txt");
                boolean isOutputForeground = pathname.getAbsolutePath().endsWith("_output_foreground.txt");
                boolean isStats = pathname.getAbsolutePath().endsWith("_stats.txt");
                //
                return !isFixedOutput && (isInput || isOutput || isStats || isOutputBackground || isOutputForeground);
            }
        }));

        for (final File file : testDataFiles) {
            String fileName = file.getName();

            // Removing the suffixes to get the test name
            String testName = fileName.replace("_input.txt", "")//
                    .replace("_output.txt", "")//
                    .replace("_output_background.txt", "").replace("_output_foreground.txt", "")//
                    .replace("_stats.txt", "");

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
            } else if (fileName.endsWith("_stats.txt")) {
                gameStats.put(testName, fileContent);
            }
        }

        List<Object[]> tests = new ArrayList<>();

        List<String> sortedKeys = new ArrayList(inputs.keySet());

        Collections.sort(sortedKeys);

        for (String key : sortedKeys) {

            if (!outputUIs.containsKey(key)) {
                throw new NullPointerException("Missing file from outputUIs test files for input test: " + key);
            }
            if (!outputBackgrounds.containsKey(key)) {
                throw new NullPointerException("Missing file from outputBackgrounds test files for input test: " + key);
            }
            if (!outputForegrounds.containsKey(key)) {
                throw new NullPointerException("Missing file from outputForegrounds test files for input test: " + key);
            }

            if (!gameStats.containsKey(key)) {
                throw new NullPointerException("Missing file from gameStats test files for input test: " + key);
            }
            // Sort the keys

            Object[] test = { key, //
                    inputs.get(key), //
                    outputUIs.get(key), //
                    outputBackgrounds.get(key), outputForegrounds.get(key), //
                    gameStats.get(key) };
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
            // Ensure that BOTH aspects are concurrently active
            Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithFlagsAndArgs(
                    Arrays.asList(new String[] { "enable.colors", "enable.stats" }), this.input);

            int exitCode = (Integer) result.get("exitCode");
            String stdOut = (String) result.get("stdOut");
            String stdError = (String) result.get("stdError");

            // Process the stdout to obtains the UI and the stats
            String[] tokens = stdOut.split(TestGameStats.OPENING_TOKEN);

            // Assert both parts are there
            Assert.assertEquals("The program did not generated all the output parts UI + GameStats", 2, tokens.length);
            String coloredUI = tokens[0];
            //
            String gameStats = tokens[1];

            // Compute the background and foreground color map for the entire output
            Triple<String, String, String> coloredOutput = new ColorFSM().demultiplex(coloredUI);
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

            // Check Game Stats
            // TODO Parse the game stats from the stats.txt file
            // TODO Parse the game stats from the stats variable
            // Check for Equality of make assertions on the fields
            TestGameStats actualStats = TestGameStats.fromString(gameStats);
            TestGameStats expectedStats = TestGameStats.fromString(this.expectedGameStats);

            Assert.assertEquals("Wrong Total Inputs", expectedStats.totalInputs, actualStats.totalInputs);
            Assert.assertEquals("Wrong Moves Count", expectedStats.totalMoves, actualStats.totalMoves);
            Assert.assertEquals("Wrong Invalid Inputs Count", expectedStats.totalInvalid, actualStats.totalInvalid);

            MatcherAssert.assertThat("Wrong Fish Status", actualStats.fishesStatus,
                    ArrayMatching.arrayContaining(expectedStats.fishesStatus));

            Integer[] actualPawMoves = ArrayUtils.toObject(actualStats.pawMoves);
            Integer[] expectedPawMoves = ArrayUtils.toObject(expectedStats.pawMoves);

            MatcherAssert.assertThat("Wrong Paw Moves", actualPawMoves,
                    ArrayMatching.arrayContaining(expectedPawMoves));

        } catch (Exception e) {
            System.err.println("ERROR FOR TEST " + this.testName);
            throw e;
        }

    }
}
