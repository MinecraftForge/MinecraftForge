package net.minecraftforge.fml.test;

import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.moddiscovery.FileSystemCache;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ZipfsPerformanceTest {
    private static final int NUM_ITERATIONS = 5000000;
    private static Path tmpJar;
    private static IModFile tmpModFile;

    @BeforeAll
    static void setup() throws IOException {
        tmpJar = Files.createTempFile("fmltest", "mod.jar");
        try (InputStream stream = ZipfsPerformanceTest.class.getResourceAsStream("/mod.jar")) {
            Files.copy(stream, tmpJar, StandardCopyOption.REPLACE_EXISTING);
        }
        tmpModFile = new IModFile() {
            @Override
            public IModLanguageProvider getLoader() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Path findResource(String className) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Supplier<Map<String, Object>> getSubstitutionMap() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Type getType() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Path getFilePath() {
                return tmpJar;
            }

            @Override
            public List<IModInfo> getModInfos() {
                throw new UnsupportedOperationException();
            }

            @Override
            public ModFileScanData getScanResult() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getFileName() {
                throw new UnsupportedOperationException();
            }

            @Override
            public IModLocator getLocator() {
                throw new UnsupportedOperationException();
            }

            @Override
            public IModFileInfo getModFileInfo() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @AfterAll
    static void tearDown() throws IOException {
        Files.delete(tmpJar);
    }

    @Test
    public void timeUncached() throws IOException {
        FileSystemCache cache = new FileSystemCache(tmpModFile, FMLConfig.FileNameCacheMode.DISABLE);
        long start = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            if (!Files.exists(cache.getPath("META-INF/mods.toml")))
                throw new RuntimeException("Expected mods.toml to be present!");
            if (Files.exists(cache.getPath("META-INF/coremods.json")))
                throw new RuntimeException("Expected coremods.json to be missing!");
            if (Files.exists(cache.getPath("assets/shield_test/lang/testabsent.blah")))
                throw new RuntimeException("Expected testabsent.blah to be missing!");
        }
        long stop = System.nanoTime();
        long tookMicros = (stop - start) / (1000 * 1000);
        System.out.println("Uncached: Took " + tookMicros + " ms");
    }

    @Test
    public void timeCachedBalanced() throws IOException {
        FileSystemCache cache = new FileSystemCache(tmpModFile, FMLConfig.FileNameCacheMode.BALANCE);
        long start = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            if (cache.getPathIfExists("META-INF/mods.toml") == null)
                throw new RuntimeException("Expected mods.toml to be present!");
            if (cache.getPathIfExists("META-INF/coremods.json") != null)
                throw new RuntimeException("Expected coremods.json to be missing!");
            if (cache.getPathIfExists("assets/shield_test/lang/testabsent.blah") != null)
                throw new RuntimeException("Expected testabsent.blah to be missing!");
        }
        long stop = System.nanoTime();
        long tookMicros = (stop - start) / (1000 * 1000);
        System.out.println("Cached (balanced): Took " + tookMicros + " ms");
    }

    @Test
    public void timeCachedFull() throws IOException {
        FileSystemCache cache = new FileSystemCache(tmpModFile, FMLConfig.FileNameCacheMode.FULL);
        long start = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            if (cache.getPathIfExists("META-INF/mods.toml") == null)
                throw new RuntimeException("Expected mods.toml to be present!");
            if (cache.getPathIfExists("META-INF/coremods.json") != null)
                throw new RuntimeException("Expected coremods.json to be missing!");
            if (cache.getPathIfExists("assets/shield_test/lang/testabsent.blah") != null)
                throw new RuntimeException("Expected testabsent.blah to be missing!");
        }
        long stop = System.nanoTime();
        long tookMicros = (stop - start) / (1000 * 1000);
        System.out.println("Cached (full): Took " + tookMicros + " ms");
    }
}
