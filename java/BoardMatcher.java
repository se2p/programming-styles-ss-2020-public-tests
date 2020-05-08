import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import java.util.Arrays;

/**
 * Custom hamcrest matcher that checks that two boards are equal.
 * 
 * @author stocker4141
 *
 */
public class BoardMatcher extends TypeSafeMatcher<String[]> {
    
    private String[] expectedBoard;
    private boolean failedBecauseOfMismatchedLineNumbers = false;
    
    public BoardMatcher(String[] expectedBoard) {
        // Store a clone of the input argument in case it gets modified.
        this.expectedBoard = expectedBoard.clone();
    }
    
    @Override
    protected boolean matchesSafely(String[] s) {
        if (expectedBoard.length != s.length) {
            failedBecauseOfMismatchedLineNumbers = true;
            return false;
        } else {
            for (int i = 0; i < expectedBoard.length; ++i) {
                if (!expectedBoard[i].equals(s[i])) {
                   return false; 
                }
            }
            return true;
        }
    }
 
    @Override
    public void describeTo(Description description) {
        if (failedBecauseOfMismatchedLineNumbers) {
            description.appendText("Board with " + expectedBoard.length + " lines!");
        } else {
            description.appendText(Arrays.toString(expectedBoard));
        }
        
    }
    
    public static Matcher<String[]> matchesBoard(String[] expectedBoard) {
        return new BoardMatcher(expectedBoard);
    }
}