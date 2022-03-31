package net.minecraftforge.launcherscript;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LauncherScript
{

    public static void main(String[] args) throws Exception
    {

        List<String> command = new ArrayList<>();
        Collections.addAll(command, findJavaBinary(), "@user_jvm_args.txt");

        command.add(String.format(Locale.ROOT, "@libraries/%s/%s_args.txt", loadMavenPath(), isWindows() ? "win" : "unix"));
        Collections.addAll(command, args);
        new ProcessBuilder(command)
                .directory(null)
                .inheritIO()
                .start().waitFor();
    }

    private static String findJavaBinary() {
        return ProcessHandle.current().info().command().orElse("java");
    }

    private static boolean isWindows()
    {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    }

    private static String loadMavenPath() throws IOException
    {
        var stream = LauncherScript.class.getClassLoader().getResourceAsStream("maven_path.txt");
        if (stream == null)
        {
            throw new IOException("Missing maven_path.txt");
        }
        try (var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
        {
            return Objects.requireNonNull(reader.readLine(), "maven_path.txt empty");
        }
    }

}
