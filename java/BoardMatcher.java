import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import java.util.Arrays;
import java.lang.StringBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * Custom hamcrest matcher that checks that two boards are equal.
 * 
 * @author stocker4141
 *
 */
public class BoardMatcher extends TypeSafeMatcher<String[]> {
    
    private String[] expectedBoard;
    private int firstDifference;
    private int firstDifferenceIndex;
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
                	firstDifference = i;
                	firstDifferenceIndex = StringUtils.indexOfDifference(s[i], expectedBoard[i]);
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
        	appendDescription(description, "expected", expectedBoard);
        }
        
    }
    
    @Override
    public void describeMismatchSafely(String[] actualBoard, Description description) {
        if (actualBoard == null) {
            description.appendText("Actual Board is null!");
        } else if (failedBecauseOfMismatchedLineNumbers) {
            description.appendText("Board with " + actualBoard.length + " lines!");
        } else {
        	appendDescription(description, "was", actualBoard);
        	appendErrorLocationMessage(description, actualBoard);
        }
        
    }
    
    public static Matcher<String[]> matchesBoard(String[] expectedBoard) {
        return new BoardMatcher(expectedBoard);
    }
    
    private void appendDescription(Description description, String stringBuilderStart, String[] board) {
    	StringBuilder builder = new StringBuilder(stringBuilderStart);
    	int boardSize = 6;
    	int startOfFirstBoard = firstDifference - (firstDifference % boardSize);
    	int endOfFirstBoard = startOfFirstBoard + boardSize;
    	// Only print the first board that contains an error.
    	for (int i = startOfFirstBoard; i < endOfFirstBoard; ++i) {
        // To instead print the whole boards with use this for:
    	// for (int i = 0; i < board.length; ++i) {
            builder.append(System.lineSeparator());
            builder.append(board[i]);
        	if (firstDifference == i) {
        		builder.append("  <<");	
        	}
        }
    	
    	builder.append(System.lineSeparator());
    	for (int i = 0; i < firstDifferenceIndex; i++) {
    		builder.append(" ");
    	}
    	builder.append("^");
        description.appendText(builder.toString());
    }
    
    private void appendErrorLocationMessage(Description description, String[] actualBoard) {
    	StringBuilder builder = new StringBuilder();
    	builder.append(System.lineSeparator());
    	builder.append("First difference at line ");
    	builder.append(firstDifference);
    	builder.append(" expected: ");
    	appendCharAtDifferingIndex(builder, expectedBoard);
    	builder.append(" got: ");
    	appendCharAtDifferingIndex(builder, actualBoard);
    	description.appendText(builder.toString());
    }
    
    private void appendCharAtDifferingIndex(StringBuilder builder, String[] board) {
    	if (firstDifferenceIndex >= board[firstDifference].length()) {
    		builder.append("End of line");
    	} else {
    		builder.append("'");
    		builder.append(board[firstDifference].charAt(firstDifferenceIndex));
    		builder.append("'");
    	}
    }
}