package net.minecraftforge.fml.common.registry;

import com.google.common.base.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Substitution test harness - tests that substitutions behave correctly
 */
@RunWith(ForgeTestRunner.class)
public class ItemBlockSubstitutionRemoveRestoreTest
{
    private ResourceLocation myDirt = new ResourceLocation("minecraft:dirt");
    private static class ItemMyDirt extends ItemMultiTexture
    {
        public ItemMyDirt() {
            super(Blocks.DIRT, Blocks.DIRT, new Mapper()
            {
                @Nullable
                public String apply(@Nonnull ItemStack p_apply_1_)
                {
                    return BlockDirt.DirtType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
                }
            });
        }
    }
    private static ItemMyDirt myDirtInstance;
    private static Item originalDirt;
    @BeforeClass
    public static void setup()
    {
        Loader.instance();
        Bootstrap.register();
        myDirtInstance = new ItemMyDirt();
        Loader.instance().setupTestHarness(new DummyModContainer(new ModMetadata() {{
            modId = "test";
        }}));
        originalDirt = new ItemStack(Blocks.DIRT).getItem();
    }

    @Test
    public void testSubstitutionRemovalAndRestore() throws Exception
    {
        GameRegistry.addSubstitutionAlias("minecraft:dirt", GameRegistry.Type.ITEM, myDirtInstance);
        PersistentRegistryManager.freezeData();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();

        final FMLControlledNamespacedRegistry<Item> itemRegistry = (FMLControlledNamespacedRegistry<Item>)PersistentRegistryManager.findRegistryByType(Item.class);

        // TEST 1: Does my substitute take effect? The substitute should be found in the registry
        ItemBlock dirtitem = (ItemBlock)itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at my block", myDirtInstance, dirtitem);

        // TEST 2: Does the substitute get removed when told by remote operation? The substitute should NOT be found in the registry
        final PersistentRegistryManager.GameDataSnapshot snapshot = PersistentRegistryManager.takeSnapshot();
        snapshot.entries.get(PersistentRegistryManager.ITEMS).substitutions.clear();
        PersistentRegistryManager.injectSnapshot(snapshot, false, false);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();

        dirtitem = (ItemBlock)itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at vanilla block", originalDirt, dirtitem);
        assertNotEquals("ItemBlock points at my block", myDirtInstance, dirtitem);

        // TEST 3: Does the substitute get restored when reverting to frozen state? The substitute should be found in the registry again
        PersistentRegistryManager.revertToFrozen();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        dirtitem = (ItemBlock)itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at my block", myDirtInstance, dirtitem);
    }
}
