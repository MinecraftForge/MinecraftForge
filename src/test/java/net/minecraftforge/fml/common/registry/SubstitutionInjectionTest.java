package net.minecraftforge.fml.common.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
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

/**
 * Substitution test harness - tests that substitutions behave correctly
 */
@RunWith(ForgeTestRunner.class)
public class SubstitutionInjectionTest
{
    private ResourceLocation myDirt = new ResourceLocation("minecraft:dirt");
    private BlockDirt toSub = new BlockDirt() {

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
        // Capture snapshot prior to registering the substitution - this is a world state "pre-substitute"
        final PersistentRegistryManager.GameDataSnapshot snapshot = PersistentRegistryManager.takeSnapshot();

        GameRegistry.addSubstitutionAlias("minecraft:dirt", GameRegistry.Type.BLOCK, toSub);
        PersistentRegistryManager.freezeData();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        // This should not throw an exception
        StatList.reinit();

        final FMLControlledNamespacedRegistry<Block> blockRegistry = (FMLControlledNamespacedRegistry<Block>)PersistentRegistryManager.findRegistryByType(Block.class);

        // TEST 1: Does my substitute take effect? The substitute should be found in the registry
        Block fnd = blockRegistry.getValue(myDirt);
        Block currDirt = Blocks.DIRT;
        assertEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertEquals("Got my dirt substitute - registry", toSub, fnd);

        // TEST 2: Does the substitute get injected when told by loading operation? The substitute should be found in the registry
        PersistentRegistryManager.injectSnapshot(snapshot, true, true);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        currDirt = Blocks.DIRT;
        assertEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertEquals("Got my dirt substitute - registry", toSub, fnd);

        // TEST 3: Does the substitute get restored when reverting to frozen state? The substitute should be found in the registry again
        PersistentRegistryManager.revertToFrozen();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(myDirt);
        currDirt = Blocks.DIRT;
        assertEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertEquals("Got my dirt substitute - registry", toSub, fnd);
    }
}
