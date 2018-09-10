/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common.registry;

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
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegistryManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Map;

/**
 * Substitution test harness - tests that substitutions behave correctly
 */
@RunWith(ForgeTestRunner.class)
public class ItemBlockSubstitutionRemoveRestoreTest
{
    private ResourceLocation myDirt = new ResourceLocation("minecraft:dirt");

    private static class ItemMyDirt extends ItemMultiTexture
    {
        public ItemMyDirt()
        {
            super(Blocks.DIRT, Blocks.DIRT, new Mapper()
            {
                @Nonnull
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
        Loader.instance().setupTestHarness(new DummyModContainer(new ModMetadata()
        {{
            modId = "test";
        }}));
        originalDirt = new ItemStack(Blocks.DIRT).getItem();
    }

    @Test
    public void testSubstitutionRemovalAndRestore() throws Exception
    {
        RegistryManager.ACTIVE.getRegistry(Item.class).register(myDirtInstance.setRegistryName(myDirt));
        GameData.freezeData();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();

        final ForgeRegistry<Item> itemRegistry = (ForgeRegistry<Item>)RegistryManager.ACTIVE.getRegistry(Item.class);

        // TEST 1: Does my substitute take effect? The substitute should be found in the registry
        ItemBlock dirtitem = (ItemBlock) itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at my block", myDirtInstance, dirtitem);

        // TEST 2: Does the substitute get removed when told by remote operation? The substitute should NOT be found in the registry
        /* Why should it not be found? Substitutions are no longer special cases
        Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot = RegistryManager.ACTIVE.takeSnapshot(false);
        snapshot.get(GameData.ITEMS).substitutions.clear();
        GameData.injectSnapshot(snapshot, false, false);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();

        dirtitem = (ItemBlock) itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at vanilla block", originalDirt, dirtitem);
        assertNotEquals("ItemBlock points at my block", myDirtInstance, dirtitem);
        */

        // TEST 3: Does the substitute get restored when reverting to frozen state? The substitute should be found in the registry again
        GameData.revertToFrozen();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        dirtitem = (ItemBlock) itemRegistry.getValue(myDirt);
        assertEquals("ItemBlock points at my block", myDirtInstance, dirtitem);
    }
}
