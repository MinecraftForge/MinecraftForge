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

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingConsumeAirEvent;
import net.minecraftforge.event.entity.living.LivingRefillAirEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(LivingAirEventTest.MODID)
public class LivingAirEventTest
{
    static final String MODID = "living_air_event_test";

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> e)
        {
            e.getRegistry().register(new MagicalAirBottleItem().setRegistryName(MODID, "magical_air_bottle"));
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class MagicalAirBottleItem extends Item
    {
        public MagicalAirBottleItem()
        {
            super(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TOOLS));
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void appendHoverText(ItemStack p_77624_1_, World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_)
        {
            super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
            CompoundNBT nbt = p_77624_1_.getTag();
            if (nbt != null && nbt.contains("air"))
            {
                p_77624_3_.add(new StringTextComponent("Air: " + nbt.getInt("air") / 20));
            }
        }

        @SubscribeEvent
        public static void onConsumeAir(LivingConsumeAirEvent e)
        {
            LivingEntity entity = e.getEntityLiving();
            ItemStack stack = entity.getMainHandItem();
            if (stack.getItem() instanceof MagicalAirBottleItem)
            {
                e.setAmount(e.getAmount() - extractAir(stack, e.getAmount()));
            }
        }

        @SubscribeEvent
        public static void onRefillAir(LivingRefillAirEvent e)
        {
            LivingEntity entity = e.getEntityLiving();
            ItemStack stack = entity.getMainHandItem();
            if (stack.getItem() instanceof MagicalAirBottleItem)
            {
                int i = Math.max(e.getAmount() - (entity.getMaxAirSupply() - entity.getAirSupply()), 0);
                e.setAmount(e.getAmount() - receiveAir(stack, i));
            }
        }

        public static int extractAir(ItemStack stack, int amount)
        {
            CompoundNBT nbt = stack.getOrCreateTag();
            amount = MathHelper.clamp(amount, 0, nbt.getInt("air"));
            if (amount == 0)
            {
                return 0;
            }
            nbt.putInt("air", nbt.getInt("air") - amount);
            return amount;
        }

        public static int receiveAir(ItemStack stack, int amount)
        {
            CompoundNBT nbt = stack.getOrCreateTag();
            amount = MathHelper.clamp(amount, 0, 100 - nbt.getInt("air"));
            if (amount == 0)
            {
                return 0;
            }
            nbt.putInt("air", nbt.getInt("air") + amount);
            return amount;
        }
    }
}
