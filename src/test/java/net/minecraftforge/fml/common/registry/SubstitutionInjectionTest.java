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
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegistryManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

/**
 * Substitution test harness - tests that substitutions behave correctly
 */
@RunWith(ForgeTestRunner.class)
public class SubstitutionInjectionTest
{
    private ResourceLocation MC_DIRT = new ResourceLocation("minecraft:dirt");
    private Block toSub = (new BlockDirt()
    {
        @Override
        @Nonnull
        public String toString()
        {
            return "SUB" + super.toString() + "SUB";
        }
    }).setRegistryName(MC_DIRT);

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
    public void testSubstitutionInjection() throws Exception
    {
        final ForgeRegistry<Block> blockRegistry = (ForgeRegistry<Block>)RegistryManager.ACTIVE.getRegistry(Block.class);
        final ForgeRegistry<Item> itemRegistry = (ForgeRegistry<Item>)RegistryManager.ACTIVE.getRegistry(Item.class);

        // Capture snapshot prior to registering the substitution - this is a world state "pre-substitute"
        final Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot = RegistryManager.ACTIVE.takeSnapshot(false);

        Block fnd = blockRegistry.getValue(MC_DIRT);
        Block vanilDirt = Blocks.DIRT;
        Block currDirt = Blocks.DIRT;
        // TEST 0: Verify that input state is correct
        assertEquals("Got vanilla dirt ", currDirt, fnd);

        // TEST 0a: Validate that the ItemBlock for Dirt points at vanilla dirt
        ItemBlock dirtitem = (ItemBlock) itemRegistry.getValue(MC_DIRT);
        assertEquals("ItemBlock points at my block", currDirt, dirtitem.getBlock());

        blockRegistry.register(toSub); //Register a new object, with the same vanilla name, Should cause the item to be replaced
        GameData.freezeData();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        // This should not throw an exception
        try
        {
            StatList.reinit();
        }
        catch (Exception e)
        {
            fail("Caught exception");
        }

        // TEST 1: Does my substitute take effect? The substitute should be found in the registry
        fnd = blockRegistry.getValue(MC_DIRT);
        currDirt = Blocks.DIRT;
        assertEquals("Got vanilla dirt - Blocks", toSub, currDirt);
        assertEquals("ObjectHolder didn't apply - Blocks and registry", currDirt, fnd);
        assertEquals("Got vanilla dirt - registry", toSub, fnd);

        // TEST 1a: Validate that the ItemBlock for Dirt now points at my dirt
        dirtitem = (ItemBlock) itemRegistry.getValue(MC_DIRT);
        assertEquals("ItemBlock points at vanilla block", toSub, dirtitem.getBlock());

        // TEST 2: Is the substitute still in the registry? The snapshot was taken before the substitute was added so it should NOT exist
        GameData.injectSnapshot(snapshot, false, true);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(MC_DIRT);
        currDirt = Blocks.DIRT;
        assertEquals("Got my dirt substitute - Blocks", vanilDirt, currDirt);
        assertEquals("ObjectHolder didn't apply - Blocks and registry", currDirt, fnd);
        assertEquals("Got my dirt substitute - registry", vanilDirt, fnd);
        dirtitem = (ItemBlock) itemRegistry.getValue(MC_DIRT);
        assertEquals("ItemBlock points at my block", vanilDirt, dirtitem.getBlock());

        // TEST 3: Does the substitute get restored when reverting to frozen state? The substitute should be found in the registry again
        GameData.revertToFrozen();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(MC_DIRT);
        currDirt = Blocks.DIRT;
        assertEquals("Got vanilla dirt - Blocks", toSub, currDirt);
        assertEquals("ObjectHolder didn't apply - Blocks and registry", currDirt, fnd);
        assertEquals("Got vanilla dirt - registry", toSub, fnd);
        dirtitem = (ItemBlock) itemRegistry.getValue(MC_DIRT);
        assertEquals("ItemBlock points at vanilla block", toSub, dirtitem.getBlock());

        // TEST 3: Is the substitute still in the registry? The snapshot was taken before the substitute, but we inject frozen data, so we SHOULD have the sub.
        GameData.injectSnapshot(snapshot, true, true);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(MC_DIRT);
        currDirt = Blocks.DIRT;
        assertEquals("Got vanilla dirt - Blocks", toSub, currDirt);
        assertEquals("ObjectHolder didn't apply - Blocks and registry", currDirt, fnd);
        assertEquals("Got vanilla dirt - registry", toSub, fnd);
        dirtitem = (ItemBlock) itemRegistry.getValue(MC_DIRT);
        assertEquals("ItemBlock points at vanilla block", toSub, dirtitem.getBlock());

        // TEST 3 repeat: Does the substitute get restored when reverting to frozen state? The substitute should be found in the registry again
        GameData.revertToFrozen();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(MC_DIRT);
        currDirt = Blocks.DIRT;
        assertEquals("Got vanilla dirt - Blocks", toSub, currDirt);
        assertEquals("ObjectHolder didn't apply - Blocks and registry", currDirt, fnd);
        assertEquals("Got vanilla dirt - registry", toSub, fnd);
        dirtitem = (ItemBlock) itemRegistry.getValue(MC_DIRT);
        assertEquals("ItemBlock points at vanilla block", toSub, dirtitem.getBlock());
    }
}
