package net.minecraftforge.server.jvmchecker;

public class Checker {
    public static boolean checkVersion(String minVersion, String maxVersion) {
        Runtime.Version currentVersion = Runtime.version();
        Runtime.Version min = Runtime.Version.parse(minVersion);
        Runtime.Version max = Runtime.Version.parse(maxVersion);
        return currentVersion.compareTo(min) >= 0 && currentVersion.compareTo(max) < 0;
    }

    public static String getCurrentVersionString() {
        return Runtime.version().toString();
    }
}
