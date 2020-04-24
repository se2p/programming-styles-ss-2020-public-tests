
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
        Map.Entry<Integer, String> result = PSTestUtils.executePreyAndHuntersWithArgs(aValidSequenceInput);
        // Assertions
        int exitCode = result.getKey();
        String consoleOutput = result.getValue();

        // Did PaH exit normally?
        Assert.assertEquals(PSTestUtils.PREY_AND_HUNTERS_CLASS_NAME + " did not exit normally!", 0, exitCode);

        // Did PaH produce any output at all?
        MatcherAssert.assertThat(PSTestUtils.PREY_AND_HUNTERS_CLASS_NAME + " did not produced any output!",
                consoleOutput, Matchers.not(blankOrNullString()));
    }

}