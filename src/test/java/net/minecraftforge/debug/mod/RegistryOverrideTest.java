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

package net.minecraftforge.debug.mod;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = RegistryOverrideTest.MODID, name = "Registry override test mod", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class RegistryOverrideTest
{
    public static final String MODID = "registry_override_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register(new BlockReplacement());
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        if (!ENABLED) return;

        event.getRegistry().register(
                new Item()
                        .setFull3D()
                        .setUnlocalizedName("stick")
                        .setCreativeTab(CreativeTabs.MATERIALS)
                        .setRegistryName("minecraft:stick")
        );
    }

    private static class BlockReplacement extends Block
    {
        AxisAlignedBB BB = FULL_BLOCK_AABB.contract(0.1, 0, 0.1);

        private BlockReplacement()
        {
            super(Material.ROCK);
            setRegistryName("minecraft", "bookshelf");
            this.setHardness(1.5F);
            this.setSoundType(SoundType.STONE).setUnlocalizedName("bookshelf");
            this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        }

        @Override
        public String toString()
        {
            return "BlockReplacement{" + this.getRegistryName() + "}";
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
        {
            if (hand == EnumHand.MAIN_HAND)
            {
                playerIn.sendMessage(new TextComponentString(worldIn.isRemote ? "Client" : "Server"));
            }
            return false;
        }
    }
}
