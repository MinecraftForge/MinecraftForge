package net.minecraftforge.test;

import net.minecraftforge.fml.relauncher.DependencyExtractor;
import net.minecraftforge.fml.relauncher.DependencyExtractor.Artifact;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import static org.junit.Assert.*;

public class DependencyExtractionTest
{
    /**
     * Base 64 encoded JAR file of the following structure:
     * container.jar
     * ├─── META-INF
     * │   └    MANIFEST.MF (contains 'ContainedDeps: dependency.jar' definition)
     * └─── dependency.jar
     *     └─── META-INF
     *         └    MANIFEST.MF (contains 'Maven-Artifact: net.minecraftforge.test:dependency:1.0:testing' definition)
     */
    private static final String JAR_BASE64 =
        "UEsDBAoAAAAAAG0V2koAAAAAAAAAAAAAAAAJAAAATUVUQS1JTkYvUEsDBBQAAAAIAFsP3Eo0MXNa" +
            "OAAAADYAAAAUAAAATUVUQS1JTkYvTUFOSUZFU1QuTUbzTczLTEstLtENSy0qzszPs1Iw1DPg5XLO" +
            "zytJzMxLTXFJLSi2UkhJLUjNS0nNS67Uy0os4uUCAFBLAwQUAAAACAACENxKhC8m9eoAAACCAQAA" +
            "DgAAAGRlcGVuZGVuY3kuamFyC/BmZuFiAIGnYre8GJAAJxD7uoY46nr6uemDlIkABTgYqvjveP2t" +
            "e3U6CMgLB2IRZGW+jn6ebq7BIXq+bqKnDbkOKPCwXoj9wBfnkHk9x7Yhu/zi+pQMHSmuvbVvfz04" +
            "qhJjeHW+xOTdE24fTvP14T8i77xz2ZonLFJ6Yk8mrrpmJ9W0zpXVfInDJ+/XF/N1zs7iD/BmZJJn" +
            "wOVYFShLAEQgu4mLQQEswMggwfB53uI7Yu8uMcLoGj1RSVEgDTEalwdhRoPMUcfhXWRbHj9eWLHw" +
            "/SXGOV5Mcch0gDcrG0gNExAeBNKrwToAUEsBAh8ACgAAAAAAbRXaSgAAAAAAAAAAAAAAAAkAJAAA" +
            "AAAAAAAQAAAAAAAAAE1FVEEtSU5GLwoAIAAAAAAAAQAYABxSdDkV7tIBHFJ0ORXu0gF8LhUZFe7S" +
            "AVBLAQIfABQAAAAIAFsP3Eo0MXNaOAAAADYAAAAUACQAAAAAAAAAIAAAACcAAABNRVRBLUlORi9N" +
            "QU5JRkVTVC5NRgoAIAAAAAAAAQAYAJwxd1Wh79IBvJqcUKHv0gG8mpxQoe/SAVBLAQIfABQAAAAI" +
            "AAIQ3EqELyb16gAAAIIBAAAOACQAAAAAAAAAIAAAAJEAAABkZXBlbmRlbmN5LmphcgoAIAAAAAAA" +
            "AQAYALLOvX6h79IBG+S8fqHv0gEVEkdboe/SAVBLBQYAAAAAAwADACEBAACnAQAAAAA=";
    private static final String JAR_NAME = "container.jar";
    private static final String DEPENDENCY_JAR_NAME = "dependency.jar";
    private static final String MAVEN_ARTIFACT_REF = "net.minecraftforge.test:dependency:1.0:testing";
    private static File workingDir;
    private static File libsDir;
    private static File modsDir;
    private static File versionedModsDir;
    private static File modJar;
    private static File expectedExtraction;

    @BeforeClass
    public static void setUp() throws Exception
    {
        workingDir = Files.createTempDirectory("temp").toFile();
        libsDir = new File(workingDir, "libraries/");
        libsDir.mkdir();
        modsDir = new File(workingDir, "mods/");
        modsDir.mkdir();
        versionedModsDir = new File(workingDir, "mods/version");
        versionedModsDir.mkdir();
        modJar = new File(modsDir, JAR_NAME);
        byte[] fileData = Base64.decodeBase64(JAR_BASE64);
        Files.write(modJar.toPath(), fileData);
        expectedExtraction = new File(libsDir, "net/minecraftforge/test/dependency/1.0/dependency-1.0-testing.jar").getAbsoluteFile();
    }

    @Test
    public void testDependencyExtraction() throws IOException
    {
        Map<Artifact, Artifact> artifacts = new HashMap<>();
        // Get extraction paths from the JAR
        JarFile jar = new JarFile(modJar);
        File targetFile = DependencyExtractor.determineTargetFile(jar, modsDir, versionedModsDir, libsDir, artifacts, DEPENDENCY_JAR_NAME, DEPENDENCY_JAR_NAME);
        assertNotNull("Extraction target must exist with our data", targetFile);
        assertEquals("Extraction target should probably unroll artifact notation", expectedExtraction, targetFile.getAbsoluteFile());
        IOUtils.closeQuietly(jar);

        // Extraction should have resulted in the one correct artifact being read
        assertEquals("One artifact should be found", 1, artifacts.size());
        for (Map.Entry<Artifact, Artifact> entry : artifacts.entrySet())
        {
            Artifact artifact = entry.getKey();
            assertThat("Artifact map serves as set, key and value should match", entry.getValue(), new BaseMatcher<Artifact>()
            {
                @Override
                public boolean matches(Object item)
                {
                    return item instanceof Artifact && ((Artifact) item).equalsAll(artifact);
                }

                @Override
                public void describeTo(Description description)
                {
                    description.appendText("artifacts match");
                }
            });
            assertEquals("Artifact should match expected value", MAVEN_ARTIFACT_REF, artifact.toGradleNotation());
        }

        // Perform actual extraction now to check it
        DependencyExtractor.inspect(workingDir, modsDir, versionedModsDir);
        assertEquals("Exactly one dependency should have been extracted", 1, DependencyExtractor.extractedDeps);
        assertEquals("Dependency should have been extracted to location", true, expectedExtraction.exists());
    }

    @AfterClass
    public static void cleanUp() throws Exception
    {
        workingDir.delete();
    }
}
