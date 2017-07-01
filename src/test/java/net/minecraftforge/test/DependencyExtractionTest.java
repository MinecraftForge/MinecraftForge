package net.minecraftforge.test;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.DependencyExtractor;
import net.minecraftforge.fml.relauncher.DependencyExtractor.Artifact;
import net.minecraftforge.fml.relauncher.ModListHelper;
import org.apache.commons.io.FileUtils;
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
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class DependencyExtractionTest
{
    /**
     * JAR included in test resources with following structure:
     * container.jar
     * ├─── META-INF
     * │   └    MANIFEST.MF (contains 'ContainedDeps: dependency.jar' definition)
     * └─── dependency.jar
     * └─── META-INF
     * └    MANIFEST.MF (contains 'Maven-Artifact: net.minecraftforge.test:dependency:1.0:testing' definition)
     */
    private static final String INTERNAL_JAR_NAME = "/dependency_extraction.jar";
    private static final String JAR_NAME = "container.jar";
    private static final String DEPENDENCY_JAR_NAME = "dependency.jar";
    private static final String MAVEN_ARTIFACT_REF = "net.minecraftforge.test:dependency:1.0:testing";
    private static final String VERSIONLESS_ARTIFACT_REF = "net.minecraftforge.test:dependency:testing";
    private static File workingDir;
    private static File libsDir;
    private static File modsDir;
    private static File versionedModsDir;
    private static File modJar;
    private static File expectedExtraction;
    private static File modListFile;

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
        Files.copy(DependencyExtractionTest.class.getResourceAsStream(INTERNAL_JAR_NAME), modJar.toPath());
        expectedExtraction = new File(libsDir, "net/minecraftforge/test/dependency/1.0/dependency-1.0-testing.jar").getAbsoluteFile();
        modListFile = new File(modsDir, "mod_list.json");
        // Perform actual extraction right away to have the right state in all tests
        DependencyExtractor.inspect(workingDir, modsDir, versionedModsDir, "./libraries/");
        // Required for mod list loading
        Launch.blackboard = new HashMap<>();
        Launch.blackboard.put("launchArgs", new HashMap<String, String>());
    }

    @Test
    public void testDependencyExtraction() throws IOException
    {
        Map<Artifact, Artifact> artifacts = new HashMap<>();
        // Get extraction paths from the JAR
        JarFile jar = new JarFile(modJar);
        File targetFile = DependencyExtractor.determineTargetFile(jar, modsDir, versionedModsDir, libsDir, artifacts, DEPENDENCY_JAR_NAME, DEPENDENCY_JAR_NAME);
        assertNotNull("Extraction target must exist with our data", targetFile);
        assertEquals("Extraction target should properly unroll artifact notation", expectedExtraction, targetFile.getAbsoluteFile());
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

        assertEquals("Exactly one dependency should have been extracted", 1, DependencyExtractor.extractedDeps);
        assertTrue("Dependency should have been extracted to expected location", expectedExtraction.exists());
    }

    @Test
    public void testModListLoading() throws Exception
    {
        assertTrue("Dependency extraction should have created a new mod list file", modListFile.exists());

        // Ensure the file was correctly written
        String json = new String(Files.readAllBytes(modListFile.toPath()), Charsets.UTF_8);
        ModListHelper.JsonModList modList = new Gson().fromJson(json, ModListHelper.JsonModList.class);
        assertEquals("Mod list file version should allow absolute and relative paths", 2, modList.version);
        assertEquals("Repository root should be relative to MC directory", "./libraries/", modList.repositoryRoot);
        assertEquals("Reference list size should be 1", 1, modList.modRef.size());
        assertEquals("Single reference should be extracted artifact", MAVEN_ARTIFACT_REF, modList.modRef.get(0));

        // Ensure FML's parsing works correctly
        ModListHelper.parseModList(workingDir);
        Map<String, File> addedMods = ModListHelper.additionalMods;
        assertEquals("Exactly one mod should have been added from the modlist", 1, addedMods.size());
        File loadedMod = addedMods.get(VERSIONLESS_ARTIFACT_REF);
        assertNotNull("Mod list should have loaded the artifact", loadedMod);
        assertEquals("Artifact should point at correct file", expectedExtraction, loadedMod.getAbsoluteFile());
    }

    @AfterClass
    public static void cleanUp() throws Exception
    {
        FileUtils.deleteDirectory(workingDir);
    }
}
