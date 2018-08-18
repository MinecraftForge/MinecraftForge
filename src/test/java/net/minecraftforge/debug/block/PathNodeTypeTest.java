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

package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Collections;

@Mod(modid = PathNodeTypeTest.MOD_ID, name = "AiNodeTypeTest", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class PathNodeTypeTest
{
    static final String MOD_ID = "ai_node_type_test";
    static final boolean ENABLED = false;

    private static final Block TEST_BLOCK = new TestBlock();

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register(TEST_BLOCK);
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (ENABLED)
            {
                ModelLoader.setCustomStateMapper(TEST_BLOCK, block -> Collections.emptyMap());
                ModelBakery.registerItemVariants(Item.getItemFromBlock(TEST_BLOCK));
            }
        }
    }

    private static final class TestBlock extends Block
    {
        TestBlock()
        {
            super(Material.ROCK);
            setRegistryName("test_block");
        }

        @Override
        public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos)
        {
            return PathNodeType.DOOR_OPEN;
        }
    }
}
