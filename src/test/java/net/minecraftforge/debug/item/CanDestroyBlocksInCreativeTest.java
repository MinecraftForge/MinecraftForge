/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//@Mod(modid = CanDestroyBlocksInCreativeTest.MODID, name = "Item.canDestroyBlockInCreative() Test", version = "1.0", acceptableRemoteVersions = "*")
public class CanDestroyBlocksInCreativeTest
{
    public static final boolean ENABLE = true;
    public static final String MODID = "item_can_destroy_blocks_in_creative_test";

    //@Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            if (!ENABLE)
                return;

            Item test = new Item()
            {
                @Override
                public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
                {
                    return false;
                }
            }.setRegistryName(MODID, "item_test")
             .setUnlocalizedName(MODID + ".item_test")
             .setCreativeTab(CreativeTabs.TOOLS);

            event.getRegistry().register(test);
        }
    }
}
*/
