package net.minecraftforge.server.jvmchecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Checker {

    static class Version implements Comparable<Version> {
        public List<Integer> version = new ArrayList<>();
        public Integer versionExt = null;

        public Version(String versionString) {
            // Legacy Version: 1.MAJOR.MINOR_PATCH
            // Modern Version: MAJOR.MINOR.PATCH+SECURITY
            // This version of this class should only handle Legacy (Java 8) versions, but no harm in supporting both formats
            Pattern versionPattern = Pattern.compile("^[0-9]+(\\.[0-9]+)*([_+][0-9]+)?$");
            if(!versionPattern.matcher(versionString).matches()) throw new IllegalArgumentException("Version String " + versionString + " could not be parsed to a version");
            List<String> versionStringParts = Arrays.stream(versionString.split("[.]")).collect(Collectors.toList()); // Split on dots
            String lastEntry = versionStringParts.get(versionStringParts.size() - 1);
            if(lastEntry.contains("_") || lastEntry.contains("+")) {
                versionStringParts.remove(versionStringParts.size() - 1);
                List<String> pair = Arrays.stream(lastEntry.split("[_+]")).collect(Collectors.toList());
                versionStringParts.add(pair.get(0));
                versionExt = Integer.parseInt(pair.get(1));
            }
            versionStringParts.forEach((String s) -> version.add(Integer.parseInt(s)));
        }

        @Override
        public int compareTo(Version other) {
            while(version.size() < other.version.size()) version.add(-1);
            while(version.size() > other.version.size()) other.version.add(-1);
            for(int i = 0; i < version.size(); i++) {
                if(version.get(i).equals(other.version.get(i))) continue;
                return version.get(i).compareTo(other.version.get(i));
            }
            return versionExt.compareTo(other.versionExt);
        }
    }

    public static Version version = new Version(System.getProperty("java.version"));
    public static Version minVersion;
    public static Version maxVersion;

    public static boolean checkVersion(String minVersionString, String maxVersionString) {
        minVersion = new Version(minVersionString);
        maxVersion = new Version(maxVersionString);
        return version.compareTo(minVersion) >= 0 && version.compareTo(maxVersion) < 0;
    }

    public static String getCurrentVersionString() {
        return System.getProperty("java.version");
    }
}
