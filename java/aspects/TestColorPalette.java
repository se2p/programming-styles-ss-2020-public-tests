package aspects;

/**
 * Test Utility class holding the mapping between colors and their various
 * representations. Note that using Strings and replacing colors with other
 * chars is by no means robust: if any of those chars appears in the UI the
 * tests will fail !!! A better approach is to represent each cell/pixel of the
 * UI with a complex object which contains information about content (char), and
 * colors (back- and fore-ground)
 * 
 * @author gambi
 *
 */
public class TestColorPalette {

    // RESET Char
    public static final String RESET = "\u001B[0m"; // ANSI_RESET
    public static final String RESET_PATTERN = "\u001B\\[0m"; // ANSI_RESET
    public static final char RESET_CHAR = '§';

    // Boat
    public static final String BOAT_BACKGROUND = "\u001B[41m"; // ANSI_RED_BACKGROUND
    public static final String BOAT_BACKGROUND_PATTERN = "\u001B\\[41m"; // ANSI_RED_BACKGROUND
    public static final char BOAT_BACKGROUND_CHAR = '@'; // 'R'; // ANSI_RED_BACKGROUND

    public static final String BOAT_FOREGROUND = "\u001B[37m"; // ANSI_WHITE
    public static final String BOAT_FOREGROUND_PATTERN = "\u001B\\[37m"; // ANSI_WHITE
    public static final char BOAT_FOREGROUND_CHAR = '^'; // 'w'; // ANSI_WHITE

    // Banner
    public static final String BANNER_BACKGROUND = "\u001B[40m"; // ANSI_BLACK_BACKGROUND
    public static final String BANNER_BACKGROUND_PATTERN = "\u001B\\[40m"; // ANSI_BLACK_BACKGROUND
    public static final char BANNER_BACKGROUND_CHAR = '#'; // 'B'; // ANSI_BLACK_BACKGROUND

    public static final String BANNER_FOREGROUND = "\u001B[33m"; // ANSI_YELLOW
    public static final String BANNER_FOREGROUND_PATTERN = "\u001B\\[33m"; // ANSI_YELLOW
    public static final char BANNER_FOREGROUND_CHAR = '}'; // 'y'; // ANSI_YELLOW

    // Sea and Board
    public static final String BOARD_BACKGROUND = "\u001B[46m"; // ANSI_CYAN_BACKGROUND
    public static final String BOARD_BACKGROUND_PATTERN = "\u001B\\[46m"; // ANSI_CYAN_BACKGROUND
    public static final char BOARD_BACKGROUND_CHAR = '%'; // 'C'; // ANSI_CYAN_BACKGROUND

    public static final String BOARD_FOREGROUND = "\u001B[34m"; // ANSI_BLUE
    public static final String BOARD_FOREGROUND_PATTERN = "\u001B\\[34m"; // ANSI_BLUE
    public static final char BOARD_FOREGROUND_CHAR = '|'; // 'b'; // ANSI_BLUE

    // PAWS - We reuse colors
    public static final String FISHER = BANNER_FOREGROUND;
    public static final String FISHER_PATTERN = BANNER_FOREGROUND_PATTERN;
    public static final char FISHER_CHAR = BANNER_FOREGROUND_CHAR;

    public static final String FISH = BOAT_FOREGROUND;
    public static final String FISH_PATTERN = BOAT_FOREGROUND_PATTERN;
    public static final char FISH_CHAR = BOAT_FOREGROUND_CHAR;

}
