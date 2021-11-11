public class CheckVersion {
    public static final boolean TESTING = false;
    public static final Runtime.Version MIN_JAVA_VERSION = Runtime.Version.parse("16.0.1");
    public static final Runtime.Version MAX_JAVA_VERSION = Runtime.Version.parse("17.0.1");

    public static void main(String [] args) {
        Runtime.Version currentVersion = Runtime.version();
        if(TESTING || !withinBounds(currentVersion)) {
            System.err.printf("Forge is not expected to function correctly on %s %s%n", System.getProperty("java.vendor"), System.getProperty("java.vendor.version"));
            System.err.printf("as its Minimum(inclusive) and Maximum(exclusive) expected working JVM versions are %s and %s respectively,%n", MIN_JAVA_VERSION, MAX_JAVA_VERSION);
            System.err.println("to acknowledge this message and continue anyway, see the instructions in the appropriate run script");
            System.exit(1);
        }
    }

    private static boolean withinBounds(Runtime.Version version) {
        return atleastMin(version) && lessThanMax(version);
    }

    private static boolean atleastMin(Runtime.Version version) {
        return version.compareTo(MIN_JAVA_VERSION) >= 0;
    }

    private static boolean lessThanMax(Runtime.Version version) {
        return version.compareTo(MAX_JAVA_VERSION) < 0;
    }
}
