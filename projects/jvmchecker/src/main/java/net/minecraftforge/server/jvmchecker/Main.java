package net.minecraftforge.server.jvmchecker;

import javax.swing.JOptionPane;
import java.awt.GraphicsEnvironment;

public class Main {
    static {
        String headlessPropVal = System.getProperty("jvmchecker.headless");
        HEADLESS = headlessPropVal != null ? Boolean.parseBoolean(headlessPropVal) : GraphicsEnvironment.isHeadless();
    }
    public static final boolean HEADLESS;
    public static final boolean FORCE_RESULT = Boolean.parseBoolean(System.getProperty("jvmchecker.testing.force_result"));
    public static final boolean FORCED_RESULT = Boolean.parseBoolean(System.getProperty("jvmchecker.testing.result"));
    public static void main(String[] args) {
        if(args.length != 2)
            if(HEADLESS)
                System.err.println("Incorrect number of arguments, must be minimum and maximum version strings");
            else
                JOptionPane.showMessageDialog(null, "JVM Checker is intended to be run by scripts, not manually", "JVM Checker Error", JOptionPane.ERROR_MESSAGE);
        else {
            if((Main.FORCE_RESULT && !Main.FORCED_RESULT) || !Checker.checkVersion(args[0], args[1])) {
                System.err.printf("Forge is not expected to function correctly on %s %s%n", System.getProperty("java.vendor"), Checker.getCurrentVersionString());
                System.err.printf("Its Minimum(inclusive) and Maximum(exclusive) expected working JVM versions are %s and %s respectively,%n", args[0], args[1]);
                System.err.println("to acknowledge this message and continue anyway, see the instructions in the appropriate run script");
                System.exit(1);
            }
        }
    }
}
