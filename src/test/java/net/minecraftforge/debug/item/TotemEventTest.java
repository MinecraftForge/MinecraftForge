package net.minecraftforge.debug.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.LivingTotemEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("totem_event_test")
@Mod.EventBusSubscriber
public class TotemEventTest {
    @SubscribeEvent
    public static void onTotemProtection(LivingTotemEvent event)
    {
        if (event.getResult() != Event.Result.DEFAULT) return;
        LivingEntity entity = event.getEntityLiving();
        if (entity.getActiveItemStack().isItemEqual(new ItemStack(Items.STICK))) {
            event.setResult(Event.Result.ALLOW);
            event.setHealth(6);
            entity.addPotionEffect(new EffectInstance(Effects.GLOWING, 1000, 1));
        } else {
            event.setResult(Event.Result.DENY);
        }
    }
}
