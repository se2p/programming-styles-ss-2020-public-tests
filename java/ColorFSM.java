import org.apache.commons.lang3.tuple.Triple;

import aspects.TestColorPalette;

/**
 * This class implements a simple state machine that extract the background and
 * foreground color information from an input String "annotated" with the colors
 * chars (from the ColorPalette.Class)
 * 
 * @author gambi
 *
 */
public class ColorFSM {

    private char backgroundColor = TestColorPalette.RESET_CHAR;
    private char foregroundColor = TestColorPalette.RESET_CHAR;

    public final String COLOR_REGEX = "\u001B\\[[;\\d]*m";

    /**
     * Produces a list of tokens corresponding to the colors from the theLineToColor
     * 
     * @param inputStringWithColorAnnotations
     * @return a triple that contains the UI, Background, Foreground. Those strings
     *         must have the exact same shape.
     */
    public Triple<String, String, String> demultiplex(String inputStringWithColorAnnotations) {

        String stringWithCharColors = inputStringWithColorAnnotations;

        // Remove all the special characters. Taken from: "TODO" somewhere on
        // stackoverflow
        String ui = stringWithCharColors.replaceAll(COLOR_REGEX, "");

        // Replace the special chars in the theLineToColor with the corresponding chars
        // so we replace complex tokens with simpler chars. Initialize the colors to be
        // RESET. We need this because the "special chars" are indeed Strings
        //
        stringWithCharColors = stringWithCharColors.replaceAll(TestColorPalette.BANNER_BACKGROUND_PATTERN,
                "" + TestColorPalette.BANNER_BACKGROUND_CHAR);

        stringWithCharColors = stringWithCharColors.replaceAll(TestColorPalette.BANNER_FOREGROUND_PATTERN,
                "" + TestColorPalette.BANNER_FOREGROUND_CHAR);
        //
        stringWithCharColors = stringWithCharColors.replaceAll(TestColorPalette.BOARD_BACKGROUND_PATTERN,
                "" + TestColorPalette.BOARD_BACKGROUND_CHAR);
        stringWithCharColors = stringWithCharColors.replaceAll(TestColorPalette.BOARD_FOREGROUND_PATTERN,
                "" + TestColorPalette.BOARD_FOREGROUND_CHAR);
        //
        stringWithCharColors = stringWithCharColors.replaceAll(TestColorPalette.BOAT_BACKGROUND_PATTERN,
                "" + TestColorPalette.BOAT_BACKGROUND_CHAR);
        stringWithCharColors = stringWithCharColors.replaceAll(TestColorPalette.BOAT_FOREGROUND_PATTERN,
                "" + TestColorPalette.BOAT_FOREGROUND_CHAR);
        //
        stringWithCharColors = stringWithCharColors.replaceAll(TestColorPalette.FISH_PATTERN,
                "" + TestColorPalette.FISH_CHAR);
        stringWithCharColors = stringWithCharColors.replaceAll(TestColorPalette.FISHER_PATTERN,
                "" + TestColorPalette.FISHER_CHAR);
        stringWithCharColors = stringWithCharColors.replaceAll(TestColorPalette.RESET_PATTERN,
                "" + TestColorPalette.RESET_CHAR);

        // Uniform newlines
        // stringWithCharColors = stringWithCharColors.replaceAll("\\R", "\n");

        if (stringWithCharColors.matches(COLOR_REGEX)) {
            throw new RuntimeException(
                    "There are still color information at this point... and it should not be the case!");
        }

        // Go over the string and compute the colors
        String background = "";
        String foreground = "";

        for (char c : stringWithCharColors.toCharArray()) {
            switch (c) {
            case TestColorPalette.RESET_CHAR:
                backgroundColor = c;
                foregroundColor = c;
                break;
            case TestColorPalette.BANNER_BACKGROUND_CHAR:
            case TestColorPalette.BOARD_BACKGROUND_CHAR:
            case TestColorPalette.BOAT_BACKGROUND_CHAR:
                backgroundColor = c;
                break;
            case TestColorPalette.BANNER_FOREGROUND_CHAR:
            case TestColorPalette.BOARD_FOREGROUND_CHAR:
            case TestColorPalette.BOAT_FOREGROUND_CHAR:
                foregroundColor = c;
                break;
            // Leave new lines intact
            case '\n':
                background += c;
                foreground += c;
                break;
            case '\r':
//                new RuntimeException("Char \r should not be there!");
                background += c;
                foreground += c;
                break;
            default:
                // Replace the char with the colors
                background += backgroundColor;
                foreground += foregroundColor;
                break;
            }

        }

//        if (ui.length() != background.length() || foreground.length() != ui.length()) {
//            throw new RuntimeException("Colored channels not the same lenght.\n" //
//                    + "ui = " + ui.length() + "\n" //
//                    + "background = " + background.length() + "\n" //
//                    + "foreground = " + foreground.length() + "\n" //
//                    + "");
//        }

        return Triple.of(ui, background, foreground);

    }

}