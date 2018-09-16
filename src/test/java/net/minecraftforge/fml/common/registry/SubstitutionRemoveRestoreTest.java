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
import net.minecraft.init.Bootstrap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;

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
    private ResourceLocation MC_DIRT = new ResourceLocation("minecraft:dirt");
    private Block toSub = (new BlockDirt(){}).setRegistryName(MC_DIRT);

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
        /* TODO: Figure this one out, We dont prevent the client from loading subs when the server says not to..
         * Why should we as long as the item is on both sides we should be fine. It's on the modder to make sure they are compatible.
         *
        final ForgeRegistry<Block> blockRegistry = (ForgeRegistry<Block>)RegistryManager.ACTIVE.getRegistry(Block.class);
        final ForgeRegistry<Item> itemRegistry = (ForgeRegistry<Item>)RegistryManager.ACTIVE.getRegistry(Item.class);
        Block vanilla = blockRegistry.getValue(MC_DIRT);
        blockRegistry.register(toSub);

        GameData.freezeData();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();


        // TEST 1: Does my substitute take effect? The substitute should be found in the registry
        Block fnd = blockRegistry.getValue(MC_DIRT);
        Block currDirt = Blocks.DIRT;
        assertEquals("Got Vanilla Dirt - Blocks", toSub, currDirt);
        assertEquals("ObjectHolder didn't apply - Blocks and registry", currDirt, fnd);
        assertEquals("Got Vanilla Dirt - registry", toSub, fnd);
        ItemBlock dirtitem = (ItemBlock)itemRegistry.getValue(MC_DIRT);
        assertEquals("ItemBlock points at my block", toSub, dirtitem.getBlock());

        // TEST 2: Does the substitute get removed when told by remote operation? The substitute should NOT be found in the registry
        final Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot = RegistryManager.ACTIVE.takeSnapshot(false);
        snapshot.entries.get(PersistentRegistryManager.BLOCKS).substitutions.clear();
        PersistentRegistryManager.injectSnapshot(snapshot, false, false);
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(MC_DIRT);
        currDirt = Blocks.DIRT;
        assertNotEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertNotEquals("Got my dirt substitute - registry", toSub, fnd);

        dirtitem = (ItemBlock) itemRegistry.getValue(MC_DIRT);
        assertEquals("ItemBlock points at vanilla block", currDirt, dirtitem.block);
        assertNotEquals("ItemBlock points at my block", toSub, dirtitem.block);

        // TEST 3: Does the substitute get restored when reverting to frozen state? The substitute should be found in the registry again
        PersistentRegistryManager.revertToFrozen();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        fnd = blockRegistry.getValue(MC_DIRT);
        currDirt = Blocks.DIRT;
        assertEquals("Got my dirt substitute - Blocks", toSub, currDirt);
        assertEquals("Got my dirt substitute - Blocks and registry", currDirt, fnd);
        assertEquals("Got my dirt substitute - registry", toSub, fnd);

        dirtitem = (ItemBlock) itemRegistry.getValue(MC_DIRT);
        assertEquals("ItemBlock points at my block", toSub, dirtitem.block);
        */
    }
}
