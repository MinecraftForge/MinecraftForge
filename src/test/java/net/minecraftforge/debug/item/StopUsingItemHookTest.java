/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(StopUsingItemHookTest.MOD_ID)
public class StopUsingItemHookTest
{

    public static final String MOD_ID = "stop_using_item_hook_test";

    private static final Logger LOGGER = LogManager.getLogger();

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> THING = ITEMS.register("thing", () -> new ThingItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1)));

    public StopUsingItemHookTest()
    {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static class ThingItem extends Item
    {

        public ThingItem(Item.Properties props)
        {
            super(props);
        }

        @Override
        public int getUseDuration(ItemStack stack)
        {
            return 100;
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
        {
            player.awardStat(Stats.ITEM_USED.get(this));
            return ItemUtils.startUsingInstantly(level, player, hand);
        }

        @Override
        public void stopUsingItem(ItemStack stack, LivingEntity e)
        {
            if (!e.getLevel().isClientSide())
            {
                LOGGER.info("Item Over!");
            }
        }

    }
}
