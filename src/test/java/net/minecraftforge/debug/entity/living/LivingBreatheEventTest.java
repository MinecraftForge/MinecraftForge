/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.entity.living;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(LivingBreatheEventTest.MODID)
public class LivingBreatheEventTest
{
    static final String MODID = "living_breathe_event_test";

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> e)
        {
            e.getRegistry().register(new WaterbreathingRelicItem().setRegistryName(MODID, "waterbreathing_relic"));
            e.getRegistry().register(new MagicalAirBottleItem().setRegistryName(MODID, "magical_air_bottle"));
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class WaterbreathingRelicItem extends Item
    {
        public WaterbreathingRelicItem()
        {
            super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
        }

        @SubscribeEvent
        public static void onBreathe(LivingBreatheEvent e)
        {
            LivingEntity entity = e.getEntityLiving();
            ItemStack stack = entity.getMainHandItem();
            if (entity.isEyeInFluid(FluidTags.WATER) && stack.getItem() instanceof WaterbreathingRelicItem)
            {
                e.setCanceled(true);
                e.setCanBreathe(true);
            }
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class MagicalAirBottleItem extends Item
    {
        public MagicalAirBottleItem()
        {
            super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag)
        {
            super.appendHoverText(stack, level, tooltip, tooltipFlag);
            CompoundTag nbt = stack.getTag();
            if (nbt != null && nbt.contains("air"))
            {
                tooltip.add(new TextComponent("Air: " + nbt.getInt("air") / 20));
            }
        }

        @SubscribeEvent
        public static void onBreathe(LivingBreatheEvent e)
        {
            LivingEntity entity = e.getEntityLiving();
            ItemStack stack = entity.getMainHandItem();
            if (stack.getItem() instanceof MagicalAirBottleItem)
            {
                e.setCanceled(true);
                if (e.canBreathe())
                {
                    int i = Math.max(e.getRefillAirAmount() - (entity.getMaxAirSupply() - entity.getAirSupply()), 0);
                    e.setRefillAirAmount(e.getRefillAirAmount() - receiveAir(stack, i));
                }
                else
                {
                    e.setConsumeAirAmount(e.getConsumeAirAmount() - extractAir(stack, e.getConsumeAirAmount()));
                }
            }
        }

        public static int extractAir(ItemStack stack, int amount)
        {
            CompoundTag nbt = stack.getOrCreateTag();
            amount = Mth.clamp(amount, 0, nbt.getInt("air"));
            if (amount == 0)
            {
                return 0;
            }
            nbt.putInt("air", nbt.getInt("air") - amount);
            return amount;
        }

        public static int receiveAir(ItemStack stack, int amount)
        {
            CompoundTag nbt = stack.getOrCreateTag();
            amount = Mth.clamp(amount, 0, 100 - nbt.getInt("air"));
            if (amount == 0)
            {
                return 0;
            }
            nbt.putInt("air", nbt.getInt("air") + amount);
            return amount;
        }
    }
}
