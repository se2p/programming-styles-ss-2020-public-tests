import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import java.util.Arrays;
import java.lang.StringBuilder;

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
        if (s == null) {
            return false;
        } else if (expectedBoard.length != s.length) {
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
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < expectedBoard.length; ++i) {
                builder.append(System.lineSeparator());
                builder.append(expectedBoard[i]);
            }
            description.appendText(builder.toString());
        }
        
    }
    
    @Override
    public void describeMismatchSafely(String[] actualBoard, Description description) {
        if (actualBoard == null) {
            description.appendText("Actual Board is null!");
        } else if (failedBecauseOfMismatchedLineNumbers) {
            description.appendText("Board with " + actualBoard.length + " lines!");
        } else {
            StringBuilder builder = new StringBuilder("was");
            for (int i = 0; i < actualBoard.length; ++i) {
                builder.append(System.lineSeparator());
                builder.append(actualBoard[i]);
            }
            description.appendText(builder.toString());
        }
        
    }
    
    public static Matcher<String[]> matchesBoard(String[] expectedBoard) {
        return new BoardMatcher(expectedBoard);
    }
}