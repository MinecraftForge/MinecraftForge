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

package net.minecraftforge.debug.entity;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = GetCollisionBoxesEventTest.MODID, name = "CollisionBoxesEventTest", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class GetCollisionBoxesEventTest
{
    public static final String MODID = "collisionboxexeventtest";

    public static final boolean ENABLED = true;
    @GameRegistry.ObjectHolder("box_block")
    private static final Block BOX_BLOCK = null;
    private static final ArrayList<BlockPos> locations = new ArrayList<>();

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register(new BoxBlock());
        }
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register(new ItemBlock(BOX_BLOCK).setRegistryName(new ResourceLocation(MODID, "box_block")));
        }
    }


    @SubscribeEvent
    public static void getBoxes(GetCollisionBoxesEvent event)
    {
        AxisAlignedBB box = event.getAabb();

        for (BlockPos pos: locations)
        {
            for (EnumFacing facing: EnumFacing.HORIZONTALS)
            {
                AxisAlignedBB temp = new AxisAlignedBB(pos).offset(new Vec3d(facing.getDirectionVec()));
                if (box.intersects(temp))
                    event.getCollisionBoxesList().add(temp);
            }
        }
    }

    private static class BoxBlock extends Block
    {

        public BoxBlock()
        {
            super(Material.ROCK);
            setRegistryName(new ResourceLocation(MODID, "box_block"));
            setCreativeTab(CreativeTabs.MISC);
        }

        @Override
        public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
        {
            locations.remove(pos);
        }

        @Override
        public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
        {
            locations.add(pos);
        }


        @Override
        public String getUnlocalizedName() {
            return "CollisionBoxes event test block";
        }

    }
}