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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DependencyExtractionTest
{
    /**
     * JAR included in test resources with following structure:
     * container.jar
     * ├─── META-INF
     * │   └    MANIFEST.MF (contains 'ContainedDeps: dependency.jar' definition)
     * ├─── dependency.jar
     * │   └─── META-INF
     * │       └    MANIFEST.MF (contains 'Maven-Artifact: net.minecraftforge.test:dependency:1.0:testing' definition)
     * ├─── dependency_external.jar (only contains a dummy file)
     * └─── dependency_external.jar.meta (contains 'Maven-Artifact: net.minecraftforge.test:dependency-with-meta:1.0' definition)
     */
    private static final String INTERNAL_JAR_NAME = "/dependency_extraction.jar";
    private static final String JAR_NAME = "container.jar";
    private static final String DEPENDENCY_JAR_NAME = "dependency.jar";
    private static final String EXTERNAL_DEPENDENCY_JAR_NAME = "dependency_external.jar";
    private static final String MAVEN_ARTIFACT_REF = "net.minecraftforge.test:dependency:1.0:testing";
    private static final String VERSIONLESS_ARTIFACT_REF = "net.minecraftforge.test:dependency:testing";
    private static final String EXTERNAL_MAVEN_ARTIFACT_REF = "net.minecraftforge.test:dependency-with-meta:1.0";
    private static final String VERSIONLESS_EXTERNAL_ARTIFACT_REF = "net.minecraftforge.test:dependency-with-meta";
    private static File workingDir;
    private static File libsDir;
    private static File modsDir;
    private static File versionedModsDir;
    private static File modJar;
    private static File expectedExtraction;
    private static File expectedExternalExtraction;
    private static File modListFile;
    private static File extractedModsFile;

    @Before
    public void setUp() throws Exception
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
        expectedExternalExtraction = new File(libsDir, "net/minecraftforge/test/dependency-with-meta/1.0/dependency-with-meta-1.0.jar").getAbsoluteFile();
        extractedModsFile = new File(modsDir, "extracted_mods.json");
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
        // Get extraction paths from the JAR
        JarFile jar = new JarFile(modJar);
        DependencyExtractor.extractedArtifacts.clear();
        File targetFile = DependencyExtractor.determineTargetFile(jar, modsDir, versionedModsDir, libsDir, DEPENDENCY_JAR_NAME, DEPENDENCY_JAR_NAME);
        assertNotNull("Extraction target must exist with our data", targetFile);
        assertEquals("Extraction target should properly unroll artifact notation", expectedExtraction, targetFile.getAbsoluteFile());
        targetFile = DependencyExtractor.determineTargetFile(jar, modsDir, versionedModsDir, libsDir, EXTERNAL_DEPENDENCY_JAR_NAME, EXTERNAL_DEPENDENCY_JAR_NAME);
        assertNotNull("Extraction target with external metadata must exist with our data", targetFile);
        assertEquals("Extraction target with external metadata should properly unroll artifact notation", expectedExternalExtraction, targetFile.getAbsoluteFile());
        IOUtils.closeQuietly(jar);

        // Extraction should have resulted in the two correct artifacts being read
        assertEquals("Two artifact should be found", 2, DependencyExtractor.extractedArtifacts.size());
        for (Map.Entry<Artifact, Artifact> entry : DependencyExtractor.extractedArtifacts.entrySet())
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
            assertTrue("Artifact should match expected value: Got '" + artifact.toGradleNotation() + "' expected '" + MAVEN_ARTIFACT_REF + "' or '" + EXTERNAL_MAVEN_ARTIFACT_REF + "'",
                MAVEN_ARTIFACT_REF.equals(artifact.toGradleNotation()) || EXTERNAL_MAVEN_ARTIFACT_REF.equals(artifact.toGradleNotation()));
        }

        assertEquals("Exactly two dependencies should have been extracted, a normal one and one with external metadata", 2, DependencyExtractor.extractedDeps);
        assertTrue("Dependency should have been extracted to expected location: " + expectedExtraction.getPath(), expectedExtraction.exists());
        assertTrue("Dependency with external metadata should have been extracted to expected location: " + expectedExternalExtraction.getPath(), expectedExternalExtraction.exists());
    }

    @Test
    public void testModListParenting() throws Exception
    {
        assertTrue("Dependency extraction should have created a new mod list file", modListFile.exists());
        String json = new String(Files.readAllBytes(modListFile.toPath()), Charsets.UTF_8);
        ModListHelper.JsonModList modList = new Gson().fromJson(json, ModListHelper.JsonModList.class);
        assertEquals("Dependency extraction should have declared the extracted mods file as parent of the base mod list", workingDir.toPath().relativize(extractedModsFile.toPath()).toString(), modList.parentList);
    }

    @Test
    public void testModListLoading() throws Exception
    {
        assertTrue("Dependency extraction should have created a new mod list file for the extracted mods", extractedModsFile.exists());

        // Ensure the file was correctly written
        String json = new String(Files.readAllBytes(extractedModsFile.toPath()), Charsets.UTF_8);
        ModListHelper.JsonModList modList = new Gson().fromJson(json, ModListHelper.JsonModList.class);
        assertEquals("Mod list file version should allow absolute and relative paths", 2, modList.version);
        assertEquals("Repository root should be relative to MC directory", "./libraries/", modList.repositoryRoot);
        assertEquals("Reference list size should be 2", 2, modList.modRef.size());
        assertEquals("Reference should be extracted artifact", MAVEN_ARTIFACT_REF, modList.modRef.get(0));
        assertEquals("Reference should be extracted artifact with external metadata", EXTERNAL_MAVEN_ARTIFACT_REF, modList.modRef.get(1));

        // Ensure FML's parsing works correctly
        ModListHelper.parseModList(workingDir);
        Map<String, File> addedMods = ModListHelper.additionalMods;
        assertEquals("Exactly two mods should have been added from the modlist", 2, addedMods.size());
        File loadedMod = addedMods.get(VERSIONLESS_ARTIFACT_REF);
        assertNotNull("Mod list should have loaded the artifact", loadedMod);
        assertEquals("Artifact should point at correct file", expectedExtraction, loadedMod.getAbsoluteFile());
        loadedMod = addedMods.get(VERSIONLESS_EXTERNAL_ARTIFACT_REF);
        assertNotNull("Mod list should have loaded the artifact with external metadata", loadedMod);
        assertEquals("Artifact with external metadata should point at correct file", expectedExternalExtraction, loadedMod.getAbsoluteFile());
    }

    @After
    public void cleanUp() throws Exception
    {
        FileUtils.deleteDirectory(workingDir);
    }
}
