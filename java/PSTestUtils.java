
/**
 * Programming Styles SoSe 20 Testing Utilities
 */
import static org.hamcrest.io.FileMatchers.anExistingDirectory;
import static org.hamcrest.io.FileMatchers.anExistingFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assume;

public class PSTestUtils {

    public final static String PREYS_AND_HUNTERS_CLASS_NAME = "PreysAndHunters";

    public final static String PREYS_AND_HUNTERS_HOME = "pah.home";
    public final static String JAVA = "pah.java";

    private static String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Check whether the requires system properties are correctly set. Not that if
     * any of those conditions fail the tests will be considered non meaningful;
     * consequently the will be SKIPPED. In other words, tests will not FAIL because
     * of non-met assumptions. </br>
     * 
     * @see <a href=
     *      "https://junit.org/junit4/javadoc/4.13/org/junit/Assume.html">https://junit.org/junit4/javadoc/4.13/org/junit/Assume.html</a>
     * 
     */
    public static void validateTheExecutionEnvironment() {
        Assume.assumeNotNull("PREYS_AND_HUNTERS_HOME is not set", getPreysAndHunter());
        Assume.assumeNotNull("Java is not set", getJava());
        // See https://www.baeldung.com/hamcrest-file-matchers
        Assume.assumeThat("Cannot find PREYS_AND_HUNTERS_HOME", new File(getPreysAndHunter()), anExistingDirectory());
        Assume.assumeThat("Cannot find " + PREYS_AND_HUNTERS_CLASS_NAME + ".class",
                new File(getPreysAndHunter(), PREYS_AND_HUNTERS_CLASS_NAME + ".class"), anExistingFile());
        // TODO Check if the java version returned by getJava() is indeed JAVA 11
    }

    public static String getPreysAndHunter() {
        return System.getProperty(PREYS_AND_HUNTERS_HOME);
    }

    /**
     * Return the specified java environment or simply java, assuming that your java
     * is configured to be the java command from the JDK 11
     * 
     * @return
     */
    public static String getJava() {
        return System.getProperty(JAVA, "java");
    }

    // https://mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static String getEncoding() {
        if (isWindows()) {
            // Wrap the option in `"` for Windows. TODO Is this really necessary?
            return '"' + "-Dfile.encoding=UTF-8" + '"';
        } else {
            return "-Dfile.encoding=UTF-8";
        }

    }

    /**
     * Execute the PreyAndHunters version pointed by getPreysAndHunter() with the
     * given arguments and return the a copy of the output generated by the program
     * as a String
     * 
     * @param args
     * @throws IOException
     */
    public static Map<String, Object> executePreysAndHuntersWithArgs(String... args) throws Exception {
        List<String> _args = new ArrayList<String>();
        _args.add(getJava());
        //
        _args.add(getEncoding());
        // In order to correctly invoke PreyAndHunters we need to set its class path
        _args.add("-cp");
        _args.add(getPreysAndHunter());
        _args.add(PREYS_AND_HUNTERS_CLASS_NAME);
        for (String arg : args) {
            _args.add(arg);
        }

        ProcessBuilder pb = new ProcessBuilder(_args);

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.getProperty("line.separator"));
        }

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorBuilder = new StringBuilder();
        String errorLine = null;
        while ((errorLine = errorReader.readLine()) != null) {
            errorBuilder.append(errorLine);
            errorBuilder.append(System.getProperty("line.separator"));
        }

        String stdOut = builder.toString();
        String stdError = errorBuilder.toString();
        int exitCode = process.waitFor();

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("exitCode", exitCode);
        result.put("stdOut", stdOut);
        result.put("stdError", stdError);

        return result;

    }

}