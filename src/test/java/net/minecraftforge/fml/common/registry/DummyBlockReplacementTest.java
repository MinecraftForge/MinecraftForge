package net.minecraftforge.fml.common.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        final PersistentRegistryManager.GameDataSnapshot snapshot = PersistentRegistryManager.takeSnapshot();
        PersistentRegistryManager.freezeData();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();

        final FMLControlledNamespacedRegistry<Block> blockRegistry = (FMLControlledNamespacedRegistry<Block>) PersistentRegistryManager.findRegistryByType(Block.class);
        Block fnd = blockRegistry.getValue(myDirt);

        assertNotEquals("Didn't find my block", fnd, testDirtBlock);
        assertEquals("Found a default air block", fnd, Blocks.AIR);

        // Insert a dummy reference to my dirt
        snapshot.entries.get(PersistentRegistryManager.BLOCKS).ids.put(myDirt, 218);
        PersistentRegistryManager.injectSnapshot(snapshot, true, true);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();

        fnd = blockRegistry.getValue(myDirt);
        assertNotEquals("Did not find my block", fnd, testDirtBlock);
        assertTrue("Found a dummy air block", fnd.getClass().getName().endsWith("BlockDummyAir"));
        final Set<ResourceLocation> dummied = PersistentRegistryManager.takeSnapshot().entries.get(PersistentRegistryManager.BLOCKS).dummied;
        assertTrue("Found my block in the dummy list", dummied.contains(myDirt));

        PersistentRegistryManager.revertToFrozen();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        assertNotEquals("Did not find my block", fnd, testDirtBlock);
        assertEquals("Found a default air block", fnd, Blocks.AIR);

        GameRegistry.register(testDirtBlock, myDirt);
        fnd = blockRegistry.getValue(myDirt);
        assertEquals("Found my block", fnd, testDirtBlock);

        // Add dummied entry in
        snapshot.entries.get(PersistentRegistryManager.BLOCKS).dummied.add(myDirt);
        // Loading locally - we should resuscitate our block
        PersistentRegistryManager.injectSnapshot(snapshot, true, true);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        assertEquals("Found my block", fnd, testDirtBlock);
        PersistentRegistryManager.revertToFrozen();

        // Sent remotely, we should NOT resuscitate our block
        PersistentRegistryManager.injectSnapshot(snapshot, false, false);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        assertNotEquals("Did not find my block", fnd, testDirtBlock);
        assertTrue("Found a dummy air block", fnd.getClass().getName().endsWith("BlockDummyAir"));
    }
}
