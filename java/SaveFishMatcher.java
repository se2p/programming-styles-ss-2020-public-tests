import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Custom hamcrest matcher that checks that a fish has fled into the ocean i.e.
 * that the fish is displayed correctly inside the ocean.
 * 
 * @author stocker4141
 *
 */
public class SaveFishMatcher extends TypeSafeMatcher<String[] > {
    
    private int fish;
    private boolean failedBecauseOfToLittleLines = false;
    
    private final static int minFish = 2;
    private final static int maxFish = 5;
    private final static int borderWidthLines = 1;
    
    public SaveFishMatcher(int fish) {
        if (fish < minFish && fish > maxFish) {
            throw new IllegalArgumentException("Only fish between " + minFish + "and" + maxFish + "are allowed!");
        }
        this.fish = fish;
    }
    
    @Override
    protected boolean matchesSafely(String[] s) {
        int boardIndex = borderWidthLines + fish - 2;
        
        if (s.length <= boardIndex) {
            failedBecauseOfToLittleLines = true;
            return false;
        } else {
            String oceanString = "\u2502   " + fish + "   \u2551";
            return s[boardIndex].endsWith(oceanString);
        }
    }

 
    @Override
    public void describeTo(Description description) {
        if (failedBecauseOfToLittleLines) {
            description.appendText("Board should have a total of 6 lines!");
        } else {
            description.appendText("Fish " + fish + " should have been in the ocean like: " 
                                    + "\u2502   " + fish + "   \u2551");
        }
    }
    
    public static Matcher<String []> saveFish(int fish) {
        return new SaveFishMatcher(fish);
    }
}