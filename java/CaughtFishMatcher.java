import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Custom hamcrest matcher that checks that a fish has been caught by the fishers i.e.
 * that the fish is displayed correctly inside the boat.
 * 
 * @author stocker4141
 *
 */
public class CaughtFishMatcher extends TypeSafeMatcher<String []> {
    
    private int fish;
    private boolean failedBecauseOfToLittleLines = false;

    private final static int minFish = 2;
    private final static int maxFish = 5;
    private final static int caughtFishOffset = 2;
    
    private final static String boatStartString = "\u2551  " + "\u2502";
    
    public CaughtFishMatcher(int fish) {
        if (fish < minFish && fish > maxFish) {
            throw new IllegalArgumentException("Only fish between " + minFish + "and" + maxFish + "are allowed!");
        }
        this.fish = fish;
    }
    
    @Override
    protected boolean matchesSafely(String[] s) {
        int boardIndex;
        // Hardcoding the fish numbers is not the most beatiful thing in the world but i do not forsee the
        // board layout changing in the future and the generic way gets convoluted fast. 
        if (fish == 2 || fish == 3) {
            boardIndex = caughtFishOffset;
        } else if (fish == 4 || fish == 5) {
            boardIndex = caughtFishOffset + 1;
        } else {
            throw new IllegalStateException("Invalid fish number: " + fish);
        }
        
        if (s.length <= boardIndex) {
            failedBecauseOfToLittleLines = true;
            return false;
        } else {
            return checkCaughtFish(s[boardIndex]);
        }
    }
 
    @Override
    public void describeTo(Description description) {
        if (failedBecauseOfToLittleLines) {
            description.appendText("Board should have a total of 6 lines!");
        } else {
            description.appendText("Fish " + fish + " should have been caught!");
        }
    }
    
    public static Matcher<String []> caughtFish(int fish) {
        return new CaughtFishMatcher(fish);
    }
    
    
    private boolean checkCaughtFish(String line) {
        if (fish == 2 || fish == 4) {
            return line.startsWith(boatStartString + fish);
        } else if (fish == 3 || fish == 5){
            // The boat can start with a space or with the fish that is one number lower in this case.
            return line.startsWith(boatStartString + " " + fish) 
                    || line.startsWith(boatStartString + (fish - 1) + fish); 
        } else {
            throw new IllegalStateException("Invalid fish number: " + fish);
        }
    }
}