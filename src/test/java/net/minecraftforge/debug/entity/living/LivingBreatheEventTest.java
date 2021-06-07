package net.minecraftforge.debug.entity.living;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(LivingBreatheEventTest.MODID)
public class LivingBreatheEventTest {

   static final String MODID = "living_breathe_event_test";

   @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
   public static class RegistryEvents {

      @SubscribeEvent
      public static void onItemsRegistry(final RegistryEvent.Register<Item> e) {
         e.getRegistry().register(new WaterbreathingRelicItem().setRegistryName(MODID, "waterbreathing_relic"));
      }

   }

   @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
   public static class WaterbreathingRelicItem extends Item {

      public WaterbreathingRelicItem() {
         super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
      }

      @SubscribeEvent
      public static void onBreathe(LivingBreatheEvent e) {
         LivingEntity entity = e.getEntityLiving();
         ItemStack stack = entity.getMainHandItem();
         if (entity.isEyeInFluid(FluidTags.WATER) && stack.getItem() instanceof WaterbreathingRelicItem) {
            e.setCanBreathe(true);
         }
      }

   }

}
