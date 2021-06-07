package net.minecraftforge.debug.entity.living;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingConsumeAirEvent;
import net.minecraftforge.event.entity.living.LivingRefillAirEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(LivingAirEventTest.MODID)
public class LivingAirEventTest {

   static final String MODID = "living_air_event_test";

   @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
   public static class RegistryEvents {

      @SubscribeEvent
      public static void onItemsRegistry(final RegistryEvent.Register<Item> e) {
         e.getRegistry().register(new MagicalAirBottleItem().setRegistryName(MODID, "magical_air_bottle"));
      }

   }

   @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
   public static class MagicalAirBottleItem extends Item {

      public MagicalAirBottleItem() {
         super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
      }

      @Override
      public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
         super.appendHoverText(stack, level, tooltip, tooltipFlag);
         CompoundTag nbt = stack.getTag();
         if (nbt != null && nbt.contains("air")) {
        	 tooltip.add(new TextComponent("Air: " + nbt.getInt("air") / 20));
         }
      }

      @SubscribeEvent
      public static void onConsumeAir(LivingConsumeAirEvent e) {
         LivingEntity entity = e.getEntityLiving();
         ItemStack stack = entity.getMainHandItem();
         if (stack.getItem() instanceof MagicalAirBottleItem) {
            e.setAmount(e.getAmount() - extractAir(stack, e.getAmount()));
         }
      }

      @SubscribeEvent
      public static void onRefillAir(LivingRefillAirEvent e) {
         LivingEntity entity = e.getEntityLiving();
         ItemStack stack = entity.getMainHandItem();
         if (stack.getItem() instanceof MagicalAirBottleItem) {
            int i = Math.max(e.getAmount() - (entity.getMaxAirSupply() - entity.getAirSupply()), 0);
            e.setAmount(e.getAmount() - receiveAir(stack, i));
         }
      }

      public static int extractAir(ItemStack stack, int amount) {
         CompoundTag nbt = stack.getOrCreateTag();
         amount = Mth.clamp(amount, 0, nbt.getInt("air"));
         if (amount == 0) {
            return 0;
         }
         nbt.putInt("air", nbt.getInt("air") - amount);
         return amount;
      }

      public static int receiveAir(ItemStack stack, int amount) {
    	  CompoundTag nbt = stack.getOrCreateTag();
         amount = Mth.clamp(amount, 0, 100 - nbt.getInt("air"));
         if (amount == 0) {
            return 0;
         }
         nbt.putInt("air", nbt.getInt("air") + amount);
         return amount;
      }

   }

}
