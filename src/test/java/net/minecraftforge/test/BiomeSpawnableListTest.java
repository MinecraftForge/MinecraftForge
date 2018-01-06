package net.minecraftforge.test;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Biomes;
import net.minecraft.init.Bootstrap;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeTestRunner;
import org.apache.logging.log4j.Level;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ForgeTestRunner.class)
public class BiomeSpawnableListTest
{
    private static Class<?> innerClass;
    private static Object inner;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void setUp() throws Exception
    {
        //Enumhelper uses ASM, need to load it with the LaunchClassLoader
        LaunchClassLoader classLoader = new LaunchClassLoader(getClassloaderURLs());
        classLoader.registerTransformer("net.minecraftforge.fml.common.asm.transformers.EnumConstructorTransformer");
        innerClass = classLoader.findClass("net.minecraftforge.test.BiomeSpawnableListTest$LaunchclassLoaded");
        inner = innerClass.getConstructors()[0].newInstance();
    }

    @Test
    public void testAddAndRemoveSpawn() throws Exception
    {
        innerClass.getDeclaredMethod("testAddAndRemoveSpawn").invoke(inner);
    }

    private static URL[] getClassloaderURLs(){
        URL[] urls;
        if (BiomeSpawnableListTest.class.getClassLoader() instanceof URLClassLoader){
            urls = ((URLClassLoader) BiomeSpawnableListTest.class.getClassLoader()).getURLs();
        } else {
            String classPath = appendPath(System.getProperty("java.class.path"), System.getProperty("env.class.path"));
            urls = pathToURLs(classPath);
        }
        return urls;
    }

    private static URL fileToURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            LogWrapper.log(Level.ERROR, e, "Could not convert {} to URL", file.toString());
            return null;
        }
    }

    //the following 2 methods are largely copied from internal sun.* classes
    public static String appendPath(String pathTo, String pathFrom) {
        if (pathTo == null || pathTo.length() == 0) {
            return pathFrom;
        } else if (pathFrom == null || pathFrom.length() == 0) {
            return pathTo;
        } else {
            return pathTo  + File.pathSeparator + pathFrom;
        }
    }

    public static URL[] pathToURLs(String path) {
        StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
        URL[] urls = new URL[st.countTokens()];
        int count = 0;
        while (st.hasMoreTokens()) {
            URL url = fileToURL(new File(st.nextToken()));
            if (url != null) {
                urls[count++] = url;
            }
        }
        if (urls.length != count) {
            URL[] tmp = new URL[count];
            System.arraycopy(urls, 0, tmp, 0, count);
            urls = tmp;
        }
        return urls;
    }

    public static class LaunchclassLoaded {
        private EnumCreatureType creatureTypeHorse;

        public LaunchclassLoaded(){
            Loader.instance();
            Bootstrap.register();
            creatureTypeHorse = EnumHelper.addCreatureType("biomespawnablelisttest:horse", AbstractHorse.class, 20, Material.AIR, true, true);
        }

        private boolean spawnableListContainsEntry(Class<? extends EntityLiving> entityClass, int weightedProb, int minGroupCount, int maxGroupCount,
                                                   EnumCreatureType creatureType, Biome biome)
        {
            boolean found = false;

            for (Biome.SpawnListEntry spawnListEntry : biome.getSpawnableList(creatureType))
            {
                if (spawnListEntry.entityClass == entityClass && spawnListEntry.itemWeight == weightedProb && spawnListEntry.minGroupCount == minGroupCount
                        && spawnListEntry.maxGroupCount == maxGroupCount)
                {
                    found = true;
                    break;
                }
            }

            return found;
        }

        public void testAddAndRemoveSpawn() throws Exception
        {
            final Class<EntityHorse> entityClass = EntityHorse.class;
            final int weightedProb = 1;
            final int minGroupCount = 1;
            final int maxGroupCount = 20;
            final Biome biome = Biomes.PLAINS;

            // Test 1: We can add a spawn for the non-vanilla EnumCreatureType
            EntityRegistry.addSpawn(entityClass, weightedProb, minGroupCount, maxGroupCount, creatureTypeHorse, biome);
            final boolean containsEntryAfterAdd = spawnableListContainsEntry(entityClass, weightedProb, minGroupCount, maxGroupCount, creatureTypeHorse, biome);
            assertTrue("The SpawnListEntry wasn't added", containsEntryAfterAdd);

            // Test 2: We can remove a spawn for the non-vanilla EnumCreatureType
            EntityRegistry.removeSpawn(entityClass, creatureTypeHorse, biome);
            final boolean containsEntryAfterRemove = spawnableListContainsEntry(entityClass, weightedProb, minGroupCount, maxGroupCount, creatureTypeHorse, biome);
            assertFalse("The SpawnListEntry wasn't removed", containsEntryAfterRemove);
        }
    }
}
