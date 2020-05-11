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
    private boolean[] matchingLines;
    private int[] differenceIndices;
    private String[] differenceStrings;
    private int firstDifference;
    private boolean failedBecauseOfMismatchedLineNumbers = false;
    
    public BoardMatcher(String[] expectedBoard) {
        // Store a clone of the input argument in case it gets modified.
        this.expectedBoard = expectedBoard.clone();
        this.matchingLines = new boolean[expectedBoard.length];
        this.differenceIndices = new int[expectedBoard.length];
        this.differenceStrings = new String[expectedBoard.length];
    }
    
    @Override
    protected boolean matchesSafely(String[] s) {
        if (s == null) {
            return false;
        } else if (expectedBoard.length != s.length) {
            failedBecauseOfMismatchedLineNumbers = true;
            return false;
        } else {
            boolean mistakeFound = false;
            for (int i = 0; i < expectedBoard.length; ++i) {
                if (!expectedBoard[i].equals(s[i])) {
                    matchingLines[i] = false;
                    differenceIndices[i] = StringUtils.indexOfDifference(s[i], expectedBoard[i]);
                    // This will contain the part of the expected board that does not match with the actual board. 
                    differenceStrings[i] = StringUtils.difference(s[i], expectedBoard[i]);
                    if (!mistakeFound) {
                    	firstDifference = i;
                    }
                    mistakeFound = true;
                } else {
                    matchingLines[i] = true;
                }
            }
            return !mistakeFound;
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
            if (!matchingLines[i]) {
                builder.append(" <!>");
                builder.append(" at: ");
                builder.append(differenceIndices[i]);
                builder.append(" difference: \"");
                builder.append(differenceStrings[i]);
                builder.append("\"");
            }
        }
        description.appendText(builder.toString());
    }    
}