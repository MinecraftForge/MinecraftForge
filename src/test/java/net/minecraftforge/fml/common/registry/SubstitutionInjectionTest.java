package net.minecraftforge.fml.common.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

/**
 * Substitution test harness - tests that substitutions behave correctly
 */
@RunWith(ForgeTestRunner.class)
public class SubstitutionInjectionTest
{
    private ResourceLocation myDirt = new ResourceLocation("minecraft:dirt");
    private BlockDirt toSub = new BlockDirt() {
        @Override
        public String toString()
        {
            return "SUB" + super.toString() + "SUB";
        }
    };

    @BeforeClass
    public static void setup()
    {
        Loader.instance();
        Bootstrap.register();
        Loader.instance().setupTestHarness(new DummyModContainer(new ModMetadata() {{
            modId = "test";
        }}));
    }

    @Test
    public void testSubstitutionInjection() throws Exception
    {
        final FMLControlledNamespacedRegistry<Block> blockRegistry = (FMLControlledNamespacedRegistry<Block>)PersistentRegistryManager.findRegistryByType(Block.class);
        final FMLControlledNamespacedRegistry<Item> itemRegistry = (FMLControlledNamespacedRegistry<Item>)PersistentRegistryManager.findRegistryByType(Item.class);

        // Capture snapshot prior to registering the substitution - this is a world state "pre-substitute"
        final PersistentRegistryManager.GameDataSnapshot snapshot = PersistentRegistryManager.takeSnapshot();

        Block fnd = blockRegistry.getValue(myDirt);
        Block currDirt = Blocks.DIRT;
        // TEST 0: Verify that input state is correct
        assertEquals("Got vanilla dirt ", currDirt, fnd);

        // TEST 0a: Validate that the ItemBlock for Dirt points at vanilla dirt
        ItemBlock dirtitem = (ItemBlock)itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at my block", currDirt, dirtitem.block);

        GameRegistry.addSubstitutionAlias("minecraft:dirt", GameRegistry.Type.BLOCK, toSub);
        PersistentRegistryManager.freezeData();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        // This should not throw an exception
        try
        {
            StatList.reinit();
        } catch (Exception e)
        {
            fail("Caught exception");
        }

        // TEST 1: Does my substitute take effect? The substitute should be found in the registry
        fnd = blockRegistry.getValue(myDirt);
        currDirt = Blocks.DIRT;
        assertEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertEquals("Got my dirt substitute - registry", toSub, fnd);

        // TEST 1a: Validate that the ItemBlock for Dirt now points at my dirt
        dirtitem = (ItemBlock)itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at my block", toSub, dirtitem.block);

        // TEST 2: Does the substitute get injected when told by loading operation? The substitute should be found in the registry
        PersistentRegistryManager.injectSnapshot(snapshot, true, true);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        currDirt = Blocks.DIRT;
        assertEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertEquals("Got my dirt substitute - registry", toSub, fnd);

        dirtitem = (ItemBlock)itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at my block", toSub, dirtitem.block);

        // TEST 3: Does the substitute get restored when reverting to frozen state? The substitute should be found in the registry again
        PersistentRegistryManager.revertToFrozen();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        currDirt = Blocks.DIRT;
        assertEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertEquals("Got my dirt substitute - registry", toSub, fnd);
        dirtitem = (ItemBlock)itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at my block", toSub, dirtitem.block);

        // TEST 2 repeat: Does the substitute get injected when told by loading operation? The substitute should be found in the registry
        PersistentRegistryManager.injectSnapshot(snapshot, true, true);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        currDirt = Blocks.DIRT;
        assertEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertEquals("Got my dirt substitute - registry", toSub, fnd);

        dirtitem = (ItemBlock)itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at my block", toSub, dirtitem.block);

        // TEST 3 repeat: Does the substitute get restored when reverting to frozen state? The substitute should be found in the registry again
        PersistentRegistryManager.revertToFrozen();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        currDirt = Blocks.DIRT;
        assertEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertEquals("Got my dirt substitute - registry", toSub, fnd);
        dirtitem = (ItemBlock)itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at my block", toSub, dirtitem.block);

    }
}
