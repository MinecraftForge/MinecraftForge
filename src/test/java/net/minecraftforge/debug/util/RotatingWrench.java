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

package net.minecraftforge.debug.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod(modid = RotatingWrench.modID, name = "Wrench Rotate Debug", version = "0.0.0", acceptableRemoteVersions = "*")
public class RotatingWrench
{
    public static final String modID = "wrenchrotatedebug";
    private static final ResourceLocation testWrenchName = new ResourceLocation(modID, "test_wrench");
    @ObjectHolder("test_wrench")
    public static final Item TEST_WRENCH = null;


    @Mod.EventBusSubscriber(modid = modID)
    public static class Registration
    {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().register(new TestWrench().setRegistryName(testWrenchName));
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            final ModelResourceLocation wrenchName = new ModelResourceLocation(testWrenchName, "inventory");
            ModelBakery.registerItemVariants(TEST_WRENCH, wrenchName);
            ModelLoader.setCustomMeshDefinition(TEST_WRENCH, is -> wrenchName);
        }
    }

    public static class TestWrench extends Item
    {
        public TestWrench()
        {
            setCreativeTab(CreativeTabs.TOOLS);
            setMaxStackSize(1);
            setHarvestLevel("wrench", 0);
        }

        @Override
        public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
        {
            ItemStack wrench = player.getHeldItem(hand);
            if (player.canPlayerEdit(pos, facing, wrench) && worldIn.isBlockModifiable(player, pos))
            {
                IBlockState blockState = worldIn.getBlockState(pos);
                Block block = blockState.getBlock();
                if (block.rotateBlock(worldIn, pos, facing))
                {
                    player.swingArm(hand);
                    worldIn.notifyNeighborsOfStateChange(pos, block, false);
                    return EnumActionResult.SUCCESS;
                }
            }
            return EnumActionResult.FAIL;
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            return "Forge Test Wrench";
        }
    }
}
