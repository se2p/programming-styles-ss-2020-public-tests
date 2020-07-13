import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

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
    private Optional<Character> ignore;

    public BoardMatcher(String[] expectedBoard) {
        this(expectedBoard, Optional.empty());
    }

    public BoardMatcher(String[] expectedBoard, char ignore) {
        this(expectedBoard, Optional.of(ignore));
    }

    private BoardMatcher(String[] expectedBoard, Optional<Character> ignore) {
        // Store a clone of the input argument in case it gets modified.
        this.expectedBoard = expectedBoard.clone();
        // This char is the one that match always, so we can simply ignore the chars
        // from the actualBoard for which the expectedBoard has some
        this.ignore = ignore;
    }

    @Override
    protected boolean matchesSafely(String[] actualBoard) {
        if (actualBoard == null) {
            return false;
        } else if (expectedBoard.length != actualBoard.length) {
            failedBecauseOfMismatchedLineNumbers = true;
            return false;
        } else {
            for (int i = 0; i < expectedBoard.length; ++i) {
                if (this.ignore.isPresent()) {
                    // Stupid code to check the first different char which is not the ignore char
                    boolean same = true;
                    for (int j = 0; j < expectedBoard[i].length() && j < actualBoard[i].length(); j++) {
                        if (expectedBoard[i].charAt(j) != actualBoard[i].charAt(j)
                                && expectedBoard[i].charAt(j) != this.ignore.get()) {
                            // We found a char that is different than expected but also not the ignore char
                            firstDifference = i;
                            firstDifferenceIndex = j;
                            same = false;
                            break;
                        }
                    }

                    if (!same) {
                        return false;
                    }

                } else {
                    if (!expectedBoard[i].equals(actualBoard[i])) {
                        // Find the first char
                        firstDifference = i;
                        firstDifferenceIndex = StringUtils.indexOfDifference(actualBoard[i], expectedBoard[i]);

                        // Otherwise this is an error
                        return false;
                    }
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

    public static Matcher<String[]> matchesBoardWithWildcardChar(String[] expectedBoard, char wildcard) {
        return new BoardMatcher(expectedBoard, wildcard);
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