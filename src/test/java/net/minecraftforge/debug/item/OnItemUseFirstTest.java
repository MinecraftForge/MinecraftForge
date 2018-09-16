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

package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;

@Mod(modid = OnItemUseFirstTest.MODID, name = "OnItemUseFirstTest", version = "0.0.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class OnItemUseFirstTest
{
    public static final boolean ENABLE = true;
    public static final String MODID = "onitemusefirsttest";
    public static CommonProxy proxy = null;

    public static abstract class CommonProxy
    {
        public void registerItem(RegistryEvent.Register<Item> event)
        {
            ItemTest.instance = new ItemTest().setCreativeTab(CreativeTabs.MISC);
            event.getRegistry().register(ItemTest.instance);
        }
    }

    public static final class ServerProxy extends CommonProxy
    {
    }

    public static final class ClientProxy extends CommonProxy
    {
        @Override
        public void registerItem(RegistryEvent.Register<Item> event)
        {
            super.registerItem(event);
            for (int i = 0; i < EnumActionResult.values().length; i++)
            {
                ModelLoader.setCustomModelResourceLocation(ItemTest.instance, i, new ModelResourceLocation(new ResourceLocation(MODID, "test_item"), "inventory"));
            }
        }
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event)
    {
        if (ENABLE)
        {
            proxy.registerItem(event);
        }
    }

    public static class ItemTest extends Item
    {
        static Item instance;
        public ItemTest()
        {
            setRegistryName(MODID, "test_item");
            setHasSubtypes(true);
        }
        @Nonnull
        @Override
        public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
        {
            ItemStack stack = player.getHeldItem(hand);
            EnumActionResult ret = EnumActionResult.values()[stack.getMetadata()];
            player.sendMessage(new TextComponentString("Called onItemUseFirst in thread " + Thread.currentThread().getName() + ", returns " + ret + " in hand " + hand.name()));
            return ret;
        }

        @Override
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
        {
            if (isInCreativeTab(tab))
            {
                for (int i = 0; i < EnumActionResult.values().length; i++)
                {
                    subItems.add(new ItemStack(this, 1, i));
                }
            }
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            return "OnItemUseFirst Returns: " + EnumActionResult.values()[stack.getMetadata()];
        }
    }
}