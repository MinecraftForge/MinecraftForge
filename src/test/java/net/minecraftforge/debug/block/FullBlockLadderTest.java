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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="test_full_ladder")
public class FullBlockLadderTest
{

    @EventHandler
    public static void onPreInit(FMLPreInitializationEvent e)
    {
        MinecraftForge.EVENT_BUS.register(BlockRegistry.class);
    }
     
    public static class BlockRegistry
    {
        @SubscribeEvent public static void onBlockRegistry(RegistryEvent.Register<Block> e) { e.getRegistry().register(new FullLadder()); }
    }
    
    public static class FullLadder extends Block
    {
        
        public FullLadder()
        {
            super(Material.ROCK);
            this.setRegistryName("test_full_ladder:the_ladder");
        }

        @Override
        public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity, EnumFacing side) { return true; }
        
    }
    
}