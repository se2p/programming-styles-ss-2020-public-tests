
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
 * This class contains a few test cases that work of the example provided in the
 * assignment description.
 * 
 * @author stocker4141
 *
 */
public class TrickyInputTest {

    public final String boatFieldTop = "\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550";
    public final String boatFieldBottom = "\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550";
    public final String oceanFieldTop = "\u2564\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557";
    public final String oceanFieldBottom = "\u2567\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D";
    public final String tileTop = "\u2564\u2550\u2550\u2550";
    public final String tileBottom = "\u2567\u2550\u2550\u2550";

    @BeforeClass
    public static void checkPrecondition() {
        /*
         * Check only once that all the required variables and configurations are
         * correctly set
         */
        PSTestUtils.validateTheExecutionEnvironment();
    }

    private void callMatchersForFinalBoardAndMessage(String[] expectedBoard, String[] expectedMessage, String stdOut) {
        String[] actualBoard = new String[expectedBoard.length];
        String[] actualMessage = new String[expectedMessage.length];

        String[] outputAsLines = stdOut.split("\\R");
        int numberOfOutputLines = outputAsLines.length;

        // Did the program produce enough output lines? This assertion guarantees the
        // parsing of the output
        // into the actual output arrays works.
        Assert.assertTrue("The program did not produce enough output lines!",
                numberOfOutputLines >= expectedBoard.length + expectedMessage.length);

        // Take the a number of lines according to the expected message from the end of
        // stdOut and store them as the
        // actual message
        for (int i = 0; i < expectedMessage.length; ++i) {
            // Count the total amount of rows backwards and then iterate forwards using i
            int outputAsLinesIndex = numberOfOutputLines - expectedMessage.length + i;
            actualMessage[i] = outputAsLines[outputAsLinesIndex];
        }

        // Take the the lines for the expected board prior to the message and store them
        // as the actual board state
        for (int i = 0; i < expectedBoard.length; ++i) {
            // Count the total amount of rows backwards and then iterate forwards using i
            int outputAsLinesIndex = numberOfOutputLines - expectedMessage.length - expectedBoard.length + i;
            actualBoard[i] = outputAsLines[outputAsLinesIndex];
        }

        // Did the program terminate with the expected message?
        // MatcherAssert.assertThat(actualMessage,
        // BoardMatcher.matchesBoard(expectedMessage));

        // Did the program terminate with the expected final board state?
        MatcherAssert.assertThat(actualBoard, BoardMatcher.matchesBoard(expectedBoard));
    }

    /**
     * This method checks that when provided with the base sequence of
     * [1,3,6,2,1,3,4,6,5,3,5,2,2,5,5,6,2,3,1,2,3,4,2, 6,4,1,6] the program prints
     * the correct ending board and then the message that no more inputs are
     * available.
     * 
     * @author stocker414
     */
    @Test(timeout = 3000)
    public void testThatGivenTrickyInputsTheProgramOutputsCorrectly() throws Exception {
        // Setup
        String[] aValidSequenceInput = new String[] { "1", "3", "6", "2", "1", "3", "4", "6", "5", "3", "5", "2", "2",
                "5", "5", "6", "2", "3", "1", "2", "3", "6", "4", "2", "4" };
        // Execution
        Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithArgs(aValidSequenceInput);

        int exitCode = (Integer) result.get("exitCode");
        String stdOut = (String) result.get("stdOut");
        String stdError = (String) result.get("stdError");

        String horizontalLine = "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500"
                + "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500";
        String[] expectedMessage = new String[] { " " + boatFieldTop + tileTop + tileTop + oceanFieldTop,
                "\u250c" + horizontalLine + "\u2510", "\u2502      Missing inputs.     \u2502",
                "\u2502      The game ends!!     \u2502", "\u2514" + horizontalLine + "\u2518",
                " " + boatFieldBottom + tileBottom + tileBottom + oceanFieldBottom };

        String[] expectedBoard = new String[] { boatFieldTop + tileTop + tileTop + oceanFieldTop,
                "\u2551  \u250c\u2500\u2500\u25101 \u2502   \u2502   \u2502   2   \u2551",
                "\u2551  \u2502  \u2502  \u2502   \u2502 3 \u2502       \u2551",
                "\u2551  \u25024 \u2502  \u2502   \u2502   \u2502       \u2551",
                "\u2551  \u2514\u2500\u2500\u25186 \u2502 5 \u2502   \u2502       \u2551",
                boatFieldBottom + tileBottom + tileBottom + oceanFieldBottom };
        // Assertions

        // Did the program exit normally?
        Assert.assertEquals(
                PSTestUtils.PREYS_AND_HUNTERS_CLASS_NAME + " did not exit normally. Error message: " + stdError + "\n",
                0, exitCode);

        // Did program produce any output at all?
        MatcherAssert.assertThat(PSTestUtils.PREYS_AND_HUNTERS_CLASS_NAME + " did not produced any output!", stdOut,
                Matchers.not(blankOrNullString()));

        callMatchersForFinalBoardAndMessage(expectedBoard, expectedMessage, stdOut);
    }

    /**
     * This method checks that when provided with the base sequence of
     * [1,3,6,2,1,3,4,6,5,3,5,2,2,5,5,6,2,3,1,2,3,4,2, 6,4,1,6] and then the moves
     * [2,5,3] - causing a lose for the fishers - the program prints the correct
     * ending board and then the message that the fish won.
     * 
     * @author stocker414
     */
    @Test(timeout = 3000)
    public void testThatGivenTrickyLosingInputsTheProgramOutputsCorrectly() throws Exception {
        // Setup

        String[] aValidSequenceInput = new String[] { "1", "3", "6", "2", "1", "3", "4", "6", "5", "3", "5", "2", "2",
                "5", "5", "6", "2", "3", "1", "2", "3", "6", "4", "2", "4", "2", "5", "3" };
        // Execution
        Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithArgs(aValidSequenceInput);

        int exitCode = (Integer) result.get("exitCode");
        String stdOut = (String) result.get("stdOut");
        String stdError = (String) result.get("stdError");

        String horizontalLine = "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500"
                + "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500";

        String[] losingMessage = new String[] { " " + boatFieldTop + tileTop + tileTop + oceanFieldTop,
                "\u250c" + horizontalLine + "\u2510", "\u2502       Go team fish!      \u2502",
                "\u2502       Finally free.      \u2502", "\u2514" + horizontalLine + "\u2518",
                " " + boatFieldBottom + tileBottom + tileBottom + oceanFieldBottom };

        String[] expectedBoard = new String[] { boatFieldTop + tileTop + tileTop + oceanFieldTop,
                "\u2551  \u250c\u2500\u2500\u25101 \u2502   \u2502   \u2502   2   \u2551",
                "\u2551  \u2502  \u2502  \u2502   \u2502   \u2502   3   \u2551",
                "\u2551  \u25024 \u2502  \u2502   \u2502   \u2502       \u2551",
                "\u2551  \u2514\u2500\u2500\u25186 \u2502   \u2502 5 \u2502       \u2551",
                boatFieldBottom + tileBottom + tileBottom + oceanFieldBottom };

        // Assertions

        // Did the program exit normally?
        Assert.assertEquals(
                PSTestUtils.PREYS_AND_HUNTERS_CLASS_NAME + " did not exit normally. Error message: " + stdError + "\n",
                0, exitCode);

        // Did program produce any output at all?
        MatcherAssert.assertThat(PSTestUtils.PREYS_AND_HUNTERS_CLASS_NAME + " did not produced any output!", stdOut,
                Matchers.not(blankOrNullString()));

        MatcherAssert.assertThat(expectedBoard, SaveFishMatcher.saveFish(2));
        MatcherAssert.assertThat(expectedBoard, CaughtFishMatcher.caughtFish(4));

        callMatchersForFinalBoardAndMessage(expectedBoard, losingMessage, stdOut);
    }

    /**
     * This method checks that when provided with the base sequence of
     * [1,3,6,2,1,3,4,6,5,3,5,2,2,5,5,6,2,3,1,2,3,4,2, 6,4,1,6] and then the moves
     * [4, 1] - causing a win for the fishers - the program prints the correct
     * ending board and then the message that the fishers won.
     */
    @Test(timeout = 3000)
    public void testThatGivenTrickyWinningInputsTheProgramOutputsCorrectly() throws Exception {
        // Setup
        String[] aValidSequenceInput = new String[] { "1", "3", "6", "2", "1", "3", "4", "6", "5", "3", "5", "2", "2",
                "5", "5", "6", "2", "3", "1", "2", "3", "6", "4", "2", "4", "4", "1" };
        // Execution
        Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithArgs(aValidSequenceInput);

        int exitCode = (Integer) result.get("exitCode");
        String stdOut = (String) result.get("stdOut");
        String stdError = (String) result.get("stdError");

        String horizontalLine = "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500"
                + "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500";
        String[] winningMessage = new String[] { "      " + boatFieldTop + oceanFieldTop,
                "\u250c" + horizontalLine + "\u2510", "\u2502 The fishing was good; it's \u2502",
                "\u2502 the catching that was bad. \u2502", "\u2514" + horizontalLine + "\u2518",
                "      " + boatFieldBottom + oceanFieldBottom };

        String[] expectedBoard = new String[] { boatFieldTop + tileTop + oceanFieldTop,
                "\u2551  \u250c\u2500\u2500\u25101 \u2502   \u2502   2   \u2551",
                "\u2551  \u2502  \u2502  \u2502 3 \u2502       \u2551",
                "\u2551  \u250245\u2502  \u2502   \u2502       \u2551",
                "\u2551  \u2514\u2500\u2500\u25186 \u2502   \u2502       \u2551",
                boatFieldBottom + tileBottom + oceanFieldBottom };

        // Did the program exit normally?
        Assert.assertEquals(
                PSTestUtils.PREYS_AND_HUNTERS_CLASS_NAME + " did not exit normally. Error message: " + stdError + "\n",
                0, exitCode);

        // Did program produce any output at all?
        MatcherAssert.assertThat(PSTestUtils.PREYS_AND_HUNTERS_CLASS_NAME + " did not produced any output!", stdOut,
                Matchers.not(blankOrNullString()));

        callMatchersForFinalBoardAndMessage(expectedBoard, winningMessage, stdOut);
    }

    /**
     * This method checks that when provided with the base sequence of
     * [1,3,6,2,1,3,4,6,5,3,5,2,2,5,5,6,2,3,1,2,3,4,2, 6,4,1,6] and then the moves
     * [2,4] - causing a tie - the program prints the correct ending board and then
     * the message that there was a tie.
     */
    @Test(timeout = 3000)
    public void testThatGivenTrickyDrawingInputsTheProgramOutputsCorrectly() throws Exception {
        // Setup
        String[] aValidSequenceInput = new String[] { "1", "3", "6", "2", "1", "3", "4", "6", "5", "3", "5", "2", "2",
                "5", "5", "6", "2", "3", "1", "2", "3", "6", "4", "2", "4", "2", "4" };
        // Execution
        Map<String, Object> result = PSTestUtils.executePreysAndHuntersWithArgs(aValidSequenceInput);

        int exitCode = (Integer) result.get("exitCode");
        String stdOut = (String) result.get("stdOut");
        String stdError = (String) result.get("stdError");

        String horizontalLine = "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500"
                + "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500";
        String[] drawMessage = new String[] { " " + boatFieldTop + tileTop + oceanFieldTop,
                "\u250c" + horizontalLine + "\u2510", "\u2502       Nice tie       \u2502",
                "\u2502         LOL!         \u2502", "\u2514" + horizontalLine + "\u2518",
                " " + boatFieldBottom + tileBottom + oceanFieldBottom };

        String[] expectedBoard = new String[] { boatFieldTop + tileTop + tileTop + oceanFieldTop,
                "\u2551  \u250c\u2500\u2500\u25101 \u2502   \u2502   \u2502   2   \u2551",
                "\u2551  \u2502  \u2502  \u2502   \u2502   \u2502   3   \u2551",
                "\u2551  \u25024 \u2502  \u2502   \u2502   \u2502       \u2551",
                "\u2551  \u2514\u2500\u2500\u25186 \u2502 5 \u2502   \u2502       \u2551",
                boatFieldBottom + tileBottom + tileBottom + oceanFieldBottom };

        // Assertions

        // Did the program exit normally?
        Assert.assertEquals(
                PSTestUtils.PREYS_AND_HUNTERS_CLASS_NAME + " did not exit normally. Error message: " + stdError + "\n",
                0, exitCode);

        // Did program produce any output at all?
        MatcherAssert.assertThat(PSTestUtils.PREYS_AND_HUNTERS_CLASS_NAME + " did not produced any output!", stdOut,
                Matchers.not(blankOrNullString()));

        callMatchersForFinalBoardAndMessage(expectedBoard, drawMessage, stdOut);

    }

}