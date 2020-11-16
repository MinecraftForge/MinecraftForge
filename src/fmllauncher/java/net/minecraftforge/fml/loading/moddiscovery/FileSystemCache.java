package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipError;

public class FileSystemCache {
    private static final boolean DEBUG = false;
    private static final int MAX_FILES_TOTAL = 30;
    private static final int MAX_FILES_PER_FOLDER = 15;
    private static final int MAX_DEPTH = 2;
    private final FileSystem fs;
    private final Set<String> possiblePrefixes;

    public FileSystemCache(final IModFile file) throws ZipError, IOException {
        this.fs = FileSystems.newFileSystem(file.getFilePath(), file.getClass().getClassLoader());
        possiblePrefixes = new HashSet<>();
        try {
            boolean finishSuccess = true;
            for (Path p : fs.getRootDirectories()) {
                finishSuccess = addAllPaths(p, MAX_DEPTH);
                if (!finishSuccess) break;
            }
            if (!finishSuccess) {
                if (DEBUG) LogManager.getLogger().warn("Too many total entries: {}", file.getFilePath().getFileName().toString());
                possiblePrefixes.clear();
            }
        } catch (IOException ex) {
            LogManager.getLogger().warn("Failed to compute possible paths for {}", file.getFilePath().getFileName().toString(), ex);
            possiblePrefixes.clear();
        }
        if (DEBUG) LogManager.getLogger().info("Build list with size {} for {}: {}", possiblePrefixes.size(), file.getFilePath().getFileName().toString(), possiblePrefixes);
    }

    private static String convertToString(Path path) {
        String pathAsString = path.toString();
        if (pathAsString.startsWith("/"))
            pathAsString = pathAsString.substring(1);
        return pathAsString;
    }

    private boolean addAllPaths(Path path, int depth) throws IOException {
        if (depth <= 0 || Files.isRegularFile(path)) {
            if (possiblePrefixes.size() >= MAX_FILES_TOTAL) return false;
            possiblePrefixes.add(convertToString(path));
        } else {
            final List<Path> subDirs = Files.list(path).collect(Collectors.toList());
            if (subDirs.size() > MAX_FILES_PER_FOLDER) {
                //Just add the entire dir, as this folder is huge
                if (DEBUG) LogManager.getLogger().warn("Dir too huge: {}", path);
                if (possiblePrefixes.size() >= MAX_FILES_TOTAL) return false;
                possiblePrefixes.add(convertToString(path));
            }
            else {
                for (final Path subDir : subDirs) {
                    boolean continueSearch = addAllPaths(subDir, depth - 1);
                    if (!continueSearch)
                        return false;
                }
            }
        }
        return true;
    }

    public Path getPath(final String... path) {
        return fs.getPath("", path);
    }

    public Path getPathIfExists(final String... path) {
        Path ret = getPath(path);
        if (!possiblePrefixes.isEmpty()) {
            boolean possibleCandidate = false;
            String pathAsString = ret.toString();
            if (pathAsString.startsWith("/"))
                pathAsString = pathAsString.substring(1);
            for (String s : possiblePrefixes) {
                if (pathAsString.startsWith(s)) {
                    possibleCandidate = true;
                    break;
                }
            }
            if (!possibleCandidate) return null;
        }
        if (Files.exists(ret))
            return ret;
        else
            return null;
    }

    public FileSystem getFilesystem() {
        return fs;
    }
}
