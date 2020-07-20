package aspects;

import java.util.Arrays;

import org.junit.Assert;

/**
 * This utility class should be used by Aspects' code as shared data for holding
 * observations during the execution. If you call it from the main method you
 * are doing something wrong. Note this has no static fields because we need two
 * separate instances for making comparisons
 * 
 * @author gambi
 *
 */
public class TestGameStats {

    public static enum FishStatus {
        ALIVE, SAFE, DEAD;
    }

    public final static String OPENING_TOKEN = ">>>>>";

    public int totalInputs = 0;
    public int totalMoves = 0;
    public int totalInvalid = 0;
    public int[] pawMoves = new int[] { 0, 0, 0, 0, 0, 0 };
    public FishStatus[] fishesStatus = new FishStatus[] { FishStatus.ALIVE, FishStatus.ALIVE, FishStatus.ALIVE,
            FishStatus.ALIVE };

    /**
     * Print to output an "easy-to-parse" version of the statistics. You need to
     * call this just before the main method returns using an aspect. Calling this
     * method directly from the main method is a violation of the style.
     */
    public void printStats() {
        // First line is the OPENING TOKEN
        // Line 0
        System.out.println(OPENING_TOKEN);
        // Next line shows the "totals"
        // Line 1
        System.out
                .println("TotalInputs=" + totalInputs + ",ValidMoves=" + totalMoves + ",InvalidMoves=" + totalInvalid);
        // Next line shows the moves for each paws
        // Lines 2
        StringBuffer sb = new StringBuffer();
        for (int pawIndex = 1; pawIndex <= 6; pawIndex++) {
            sb.append(pawIndex + "=" + pawMoves[pawIndex - 1]);
            sb.append(",");
        }
        System.out.println(sb.toString());
        // Line 3
        // Next line show that status of each fish
        sb = new StringBuffer();
        for (int pawIndex = 2; pawIndex <= 5; pawIndex++) {
            sb.append(pawIndex + "=" + fishesStatus[pawIndex - 2]);
            sb.append(",");
        }
        System.out.println(sb.toString());
    }

    /**
     * Parse the given string into a GameState-like object. It assumes that the
     * opening token as been already removed!
     * 
     * @param expectedGameStats
     * @return
     */
    public static TestGameStats fromString(String expectedGameStats) {
        String[] lines = expectedGameStats.split("\\R");

        Assert.assertEquals("Game stats string contains the wrong number of lines!\n" + Arrays.toString(lines), 4,
                lines.length);

        TestGameStats parsed = new TestGameStats();
        // Parse the string
        // Line 0 - skip the token
        // Line 1
        parsed.totalInputs = Integer.parseInt(lines[1].split(",")[0].split("=")[1]);
        parsed.totalMoves = Integer.parseInt(lines[1].split(",")[1].split("=")[1]);
        parsed.totalInvalid = Integer.parseInt(lines[1].split(",")[2].split("=")[1]);

        // Line 2
        for (int pawIndex = 1; pawIndex <= 6; pawIndex++) {
            parsed.pawMoves[pawIndex - 1] = Integer.parseInt(lines[2].split(",")[pawIndex - 1].split("=")[1]);
        }

        // Line 3
        for (int pawIndex = 2; pawIndex <= 5; pawIndex++) {
            parsed.fishesStatus[pawIndex - 2] = FishStatus.valueOf(lines[3].split(",")[pawIndex - 2].split("=")[1]);
        }

        return parsed;
    }

}
