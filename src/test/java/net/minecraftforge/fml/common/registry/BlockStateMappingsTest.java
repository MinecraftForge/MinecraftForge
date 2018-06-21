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
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.registries.GameData;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Test block state mappings bidirectionality
 */
@RunWith(ForgeTestRunner.class)
public class BlockStateMappingsTest
{
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
    public void testBlockStates()
    {
        // stems have a problem where the blockstate for meta 0 is not the same in the blockstate map and from block.getStateFromMeta
        // This test asserts that the two values should be equivalent
        // Specifically, in vanilla the state for meta 0 in the blockstatemap is facing=east,age=0 but the block says it's facing=up,age=0
        Block bl = Blocks.MELON_STEM;
        int id = Block.getIdFromBlock(bl);
        for (int meta = 0; meta < 8; meta++)
        {
            int realbsm = id << 4 | meta; // computed blockstateid for this meta
            IBlockState realst = bl.getStateFromMeta(meta); // The state that the block assigns to this meta
            int bsm = GameData.getBlockStateIDMap().get(realst); // The blockstateid for the meta's state
            IBlockState foundst = GameData.getBlockStateIDMap().getByValue(realbsm); // The state that is stored for the computed blockstateid
            assertEquals("Got computed blockstate ids that match", realbsm, bsm);
            assertEquals("Got equal states", realst, foundst);
        }
    }

}
