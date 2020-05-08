
//See https://www.baeldung.com/hamcrest-text-matchers
import static org.hamcrest.text.IsBlankString.blankOrNullString;

import java.util.Map;

//See https://junit.org/junit4/javadoc/latest/deprecated-list.html
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class contains few basic test cases that illustrate how the program Prey
 * and Hunters can be tested at system level.
 * 
 * @author gambi
 *
 */
public class BasicTest {

    @BeforeClass
    public static void checkPrecondition() {
        /*
         * Check only once that all the required variables and configurations are
         * correctly set
         */
        PSTestUtils.validateTheExecutionEnvironment();
    }

    @Test(timeout = 3000)
    public void testThatGivenCorrectInputsTheProgramExitNormally() throws Exception {
        // Setup
        String[] aValidSequenceInput = new String[] { "1", "2", "3" };
        // Execution
        Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithArgs(aValidSequenceInput);

        int exitCode = (Integer) result.get("exitCode");
        String stdOut = (String) result.get("stdOut");
        String stdError = (String) result.get("stdError");

        // Assertions

        // Did the program exit normally?
        Assert.assertEquals(
                PSTestUtils.PREYS_AND_HUNTERS_CLASS_NAME + " did not exit normally. Error message: " + stdError + "\n",
                0, exitCode);

        // Did program produce any output at all?
        MatcherAssert.assertThat(PSTestUtils.PREYS_AND_HUNTERS_CLASS_NAME + " did not produced any output!", stdOut,
                Matchers.not(blankOrNullString()));
    }

    @Test(timeout = 3000)
    public void testGUIHasTheCorrectNumberOfLinesForNoInput() throws Exception {
        // Setup
        String[] aValidSequenceInput = new String[] {};

        /*
         * This test should result in the following: 6 lines for the initial board + 6
         * lines for the board with the error message
         */
        int expectedLines = 6 + 6;

        // Execution
        Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithArgs(aValidSequenceInput);

        String stdOut = (String) result.get("stdOut");

        // Assertions

        String[] lines = stdOut.split("\n");
        Assert.assertEquals("The GUI has the wrong number of lines", expectedLines, lines.length);
    }

    @Test(timeout = 3000)
    public void testGUIHasTheCorrectNumberOfLinesForOneInput() throws Exception {
        // Setup
        String[] aValidSequenceInput = new String[] { "1" };

        /*
         * This test should result in the following: 6 lines for the initial board + 6
         * lines for the first input + 6 lines for board with the error message
         */
        int expectedLines = 6 + 6 + 6;

        // Execution
        Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithArgs(aValidSequenceInput);

        String stdOut = (String) result.get("stdOut");

        // Assertions

        String[] lines = stdOut.split("\n");
        Assert.assertEquals("The GUI has the wrong number of lines", expectedLines, lines.length);
    }

    @Test(timeout = 3000)
    public void testGUIHasTheCorrectNumberOfLinesForTwoInputs() throws Exception {
        // Setup
        String[] aValidSequenceInput = new String[] { "1", "1" };

        /*
         * This test should result in the following: 6 lines for the initial board + 6
         * lines for the first input + 6 lines for the second input + 6 lines for the
         * board and the error message
         */
        int expectedLines = 6 + 6 + 6 + 6;

        // Execution
        Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithArgs(aValidSequenceInput);

        String stdOut = (String) result.get("stdOut");

        // Assertions

        String[] lines = stdOut.split("\n");
        Assert.assertEquals("The GUI has the wrong number of lines", expectedLines, lines.length);
    }

    @Test(timeout = 3000)
    public void testGUIHasTheCorrectNumberOfLinesForFishWinningInputs() throws Exception {
        // Setup
        String[] aValidSequenceInput = new String[] { //
                "2", "2", "2", "2", "2", "2", // Fish 2 is safe
                "3", "3", "3", "3", "3", "3", // Fish 3 is safe
                "4", "4", "4", "4", "4", "4", // Fish 4 is safe
                "5", "5", "5", "5", "5", "5" // Fish 5 is safe
        };

        /*
         * This test should result in the following: 6 lines for the initial board + 6
         * lines for the next 24 inputs NOTE: there are no additional 6 lines for the
         * final message !
         */
        int expectedLines = 6 + 24 * 6;

        // Execution
        Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithArgs(aValidSequenceInput);

        String stdOut = (String) result.get("stdOut");

        // Assertions
        String[] lines = stdOut.split("\n");
        Assert.assertEquals("The GUI has the wrong number of lines", expectedLines, lines.length);
    }

    @Test(timeout = 3000)
    public void testGUIHasTheCorrectNumberOfLinesForFishersWinningInputs() throws Exception {
        // Setup
        String[] aValidSequenceInput = new String[] { //
                "1", "1", "1", "1", "1", "1" // Fishers caught all the fished at once
        };

        /*
         * This test should result in the following: 6 lines for the initial board + 6
         * lines for next 6 inputs NOTE: there are no additional 6 lines for the final
         * message !
         */
        int expectedLines = 6 + 6 * 6;

        // Execution
        Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithArgs(aValidSequenceInput);

        String stdOut = (String) result.get("stdOut");

        // Assertions

        String[] lines = stdOut.split("\n");
        Assert.assertEquals("The GUI has the wrong number of lines", expectedLines, lines.length);
    }

}
