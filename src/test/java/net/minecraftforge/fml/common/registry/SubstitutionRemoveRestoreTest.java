package net.minecraftforge.fml.common.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Substitution test harness - tests that substitutions behave correctly
 */
@RunWith(ForgeTestRunner.class)
public class SubstitutionRemoveRestoreTest
{
    private ResourceLocation myDirt = new ResourceLocation("minecraft:dirt");
    private BlockDirt toSub = new BlockDirt()
    {

    };

    @BeforeClass
    public static void setup()
    {
        Loader.instance();
        Bootstrap.register();
        Loader.instance().setupTestHarness(new DummyModContainer(new ModMetadata()
        {{
            modId = "test";
        }}));
    }

    @Test
    public void testSubstitutionRemovalAndRestore() throws Exception
    {
        GameRegistry.addSubstitutionAlias("minecraft:dirt", GameRegistry.Type.BLOCK, toSub);
        PersistentRegistryManager.freezeData();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();

        final FMLControlledNamespacedRegistry<Block> blockRegistry = (FMLControlledNamespacedRegistry<Block>) PersistentRegistryManager.findRegistryByType(Block.class);
        final FMLControlledNamespacedRegistry<Item> itemRegistry = (FMLControlledNamespacedRegistry<Item>) PersistentRegistryManager.findRegistryByType(Item.class);

        // TEST 1: Does my substitute take effect? The substitute should be found in the registry
        Block fnd = blockRegistry.getValue(myDirt);
        Block currDirt = Blocks.DIRT;
        assertEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertEquals("Got my dirt substitute - registry", toSub, fnd);

        ItemBlock dirtitem = (ItemBlock) itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at my block", toSub, dirtitem.block);

        // TEST 2: Does the substitute get removed when told by remote operation? The substitute should NOT be found in the registry
        final PersistentRegistryManager.GameDataSnapshot snapshot = PersistentRegistryManager.takeSnapshot();
        snapshot.entries.get(PersistentRegistryManager.BLOCKS).substitutions.clear();
        PersistentRegistryManager.injectSnapshot(snapshot, false, false);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        currDirt = Blocks.DIRT;
        assertNotEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertNotEquals("Got my dirt substitute - registry", toSub, fnd);

        dirtitem = (ItemBlock) itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at vanilla block", currDirt, dirtitem.block);
        assertNotEquals("ItemBlock points at my block", toSub, dirtitem.block);

        // TEST 3: Does the substitute get restored when reverting to frozen state? The substitute should be found in the registry again
        PersistentRegistryManager.revertToFrozen();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        currDirt = Blocks.DIRT;
        assertEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertEquals("Got my dirt substitute - registry", toSub, fnd);

        dirtitem = (ItemBlock) itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at my block", toSub, dirtitem.block);
    }
}
