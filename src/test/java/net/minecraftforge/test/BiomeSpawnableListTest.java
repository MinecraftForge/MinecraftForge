package net.minecraftforge.test;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Biomes;
import net.minecraft.init.Bootstrap;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeTestRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ForgeTestRunner.class)
public class BiomeSpawnableListTest
{
    private static EnumCreatureType creatureTypeHorse;

    @BeforeClass
    public static void setUp() throws Exception
    {
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

    @Test
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
