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
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.ArrayMatching;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import aspects.TestGameStats;

/**
 * 
 * This test is based on {@link FileTest} by Fabian Schliski. It assumes the
 * availability of an additional file that encode the expectations about the
 * game stats that must be produced by the aspect and printed by the end of the
 * output file. It works as follows:
 * <ol>
 * <li>feed the input file to the program after enabling the game stats aspect
 * and capture the corresponding output.</li>
 * <li>split the output in two parts:
 * <ul>
 * <li>the UI, which contains only the "regular" UI chars</li>
 * <li>the Game stats, which contains only the game stats</li>
 * </ul>
 * <li>
 * <li>check the expectations on the UI part using the BoardMatcher and the
 * expectations on the game stats via basic assumptions on the fields of the
 * {@link TestGameStats} class</li>
 * </ol>
 * 
 * @author gambi
 *
 */
@Category({ Assignment3.class })
@RunWith(Parameterized.class)
public class GameStatsFileTest {

    private String testName;
    private String[] input;
    private String expectedOutputUI;
    private String expectedGameStats;

    public GameStatsFileTest(String testName, String[] input, //
            String expectedOutput, String expectedGameStats) {
        this.testName = testName;
        this.input = input;
        this.expectedOutputUI = expectedOutput;
        this.expectedGameStats = expectedGameStats;
    }

    /**
     * Return a list of TestName, input, output, stats files
     * 
     * @return
     */
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
        Map<String, String> output = new HashMap<>();
        Map<String, String> stats = new HashMap<>();

        List<File> testDataFiles = Arrays.asList(testDataFolder.listFiles(new FileFilter() {

            // We can about input and output files only but not "fixed"
            @Override
            public boolean accept(File pathname) {
                boolean isFixedOutput = pathname.getAbsolutePath().contains("fixed");
                boolean isInput = pathname.getAbsolutePath().endsWith("_input.txt");
                boolean isOutput = pathname.getAbsolutePath().endsWith("_output.txt");
                boolean isStats = pathname.getAbsolutePath().endsWith("_stats.txt");
                //
                return !isFixedOutput && (isInput || isOutput || isStats);
            }
        }));

        for (final File file : testDataFiles) {
            String fileName = file.getName();
            // Removing the suffixes from the file name to get the test name
            String testName = fileName.replace("_input.txt", "").replace("_output.txt", "").replace("_stats.txt", "");

            // Reading the file contents
            String fileContent = readFile(file.getAbsolutePath());

            if (fileName.endsWith("_input.txt")) {
                inputs.put(testName, Stream.of(fileContent.split(" ")).map(s -> s.trim()).toArray(String[]::new));
            } else if (fileName.endsWith("_output.txt")) {
                output.put(testName, fileContent);
            } else if (fileName.endsWith("_stats.txt")) {
                stats.put(testName, fileContent);
            }
        }

        List<Object[]> tests = new ArrayList<>();

        // TODO Replace with Assume

        // Invoke sorted
        List<String> sortedKeys = new ArrayList(inputs.keySet());

        Collections.sort(sortedKeys);

        for (String key : sortedKeys) {
            if (!output.containsKey(key)) {
                throw new NullPointerException("Missing output test file for test: " + key);
            }

            if (!stats.containsKey(key)) {
                throw new NullPointerException("Missing stats test file for test: " + key);
            }
            Object[] test = { key, //
                    inputs.get(key), //
                    output.get(key), //
                    stats.get(key) };
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
    public void testInputOutputAndStatsFile() throws Exception {

        try {
            // Execute PAH with stats aspect enabled
            Map<String, Object> result = PSTestUtils
                    .executePreysAndHuntersWithFlagsAndArgs(Arrays.asList(new String[] { "enable.stats" }), this.input);
            //
            int exitCode = (Integer) result.get("exitCode");
            String stdOut = (String) result.get("stdOut");
            String stdError = (String) result.get("stdError");

            // Process the stdout to obtains the UI and the stats
            String[] tokens = stdOut.split(TestGameStats.OPENING_TOKEN);

            // Assert both parts are there
            Assert.assertEquals("The program did not generated all the output parts UI + GameStats", 2, tokens.length);
            String ui = tokens[0];
            //
            String stats = tokens[1];

            // Assert that output is still OK!
            // Does the program output match the expected one?
            String[] outputAsLines = ui.split("\\R");
            String[] expectedAsLines = this.expectedOutputUI.split("\\R");
            MatcherAssert.assertThat("The GUI output for test " + this.testName + " is not as expected.", outputAsLines,
                    BoardMatcher.matchesBoard(expectedAsLines));

            // TODO Parse the game stats from the stats.txt file
            // TODO Parse the game stats from the stats variable
            // Check for Equality of make assertions on the fields
            TestGameStats actualStats = TestGameStats.fromString(stats);
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

            // Game Stats produces as output 4 lines
            // 1 - Opening Token
            // 2 - Total moves and inputs
            // 3 - Paws' moves
            // 4 - Status of fishes after game ends

//                    foregroundAsLines, BoardMatcher.matchesBoardWithWildcardChar(expectedForegroundAsLines, ' '));
        } catch (Exception e) {
            System.err.println("ERROR FOR TEST " + this.testName);
            throw e;
        }

    }
}
