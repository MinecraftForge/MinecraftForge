package net.minecraftforge.fml.common.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegistryManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Dummy block replacement test
 */
@RunWith(ForgeTestRunner.class)
public class DummyBlockReplacementTest
{
    private ResourceLocation myDirt = new ResourceLocation("test:dirt");
    private BlockDirt testDirtBlock = new BlockDirt()
    {

    };

    @BeforeClass
    public static void setup()
    {
        System.setProperty("fml.queryResult", "confirm");
        System.setProperty("fml.doNotBackup", "true");
        Loader.instance();
        Bootstrap.register();
        Loader.instance().setupTestHarness(new DummyModContainer(new ModMetadata()
        {{
            modId = "test";
        }}));
    }

    @Test
    public void testDummyBlockReplacement()
    {
        final Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot = Maps.newHashMap();
        GameData.freezeData();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();

        final ForgeRegistry<Block> blockRegistry = (ForgeRegistry<Block>)RegistryManager.ACTIVE.getRegistry(Block.class);
        Block fnd = blockRegistry.getValue(myDirt);

        assertNotEquals("Didn't find my block", fnd, testDirtBlock);
        assertEquals("Found a default air block", fnd, Blocks.AIR);

        // Insert a dummy reference to my dirt
        snapshot.put(GameData.BLOCKS, new ForgeRegistry.Snapshot());
        snapshot.get(GameData.BLOCKS).ids.put(myDirt, 218);
        GameData.injectSnapshot(snapshot, true, true);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();

        fnd = blockRegistry.getValue(myDirt);
        assertNotEquals("Did not find my block", fnd, testDirtBlock);
        assertTrue("Found a dummy air block", fnd.getClass().getName().endsWith("BlockDummyAir"));
        final Set<ResourceLocation> dummied = RegistryManager.ACTIVE.takeSnapshot(false).get(GameData.BLOCKS).dummied;
        assertTrue("Found my block in the dummy list", dummied.contains(myDirt));

        GameData.revertToFrozen();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        assertNotEquals("Did not find my block", fnd, testDirtBlock);
        assertEquals("Found a default air block", fnd, Blocks.AIR);

        RegistryManager.ACTIVE.getRegistry(Block.class).register(testDirtBlock.setRegistryName(myDirt));
        fnd = blockRegistry.getValue(myDirt);
        assertEquals("Found my block", fnd, testDirtBlock);

        // Add dummied entry in
        snapshot.get(GameData.BLOCKS).dummied.add(myDirt);
        // Loading locally - we should resuscitate our block
        GameData.injectSnapshot(snapshot, true, true);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        assertEquals("Found my block", fnd, testDirtBlock);
        GameData.revertToFrozen();

        // Sent remotely, we should NOT resuscitate our block
        GameData.injectSnapshot(snapshot, false, false);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        assertNotEquals("Did not find my block", fnd, testDirtBlock);
        assertTrue("Found a dummy air block", fnd.getClass().getName().endsWith("BlockDummyAir"));
    }
}
