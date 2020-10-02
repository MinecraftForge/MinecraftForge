package net.minecraftforge.debug.entity.living;

import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("living_conversion_event_test")
public class LivingConversionEventTest
{
    public LivingConversionEventTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::canLivingConversion);
        MinecraftForge.EVENT_BUS.addListener(this::onLivingConversion);
    }

    public void canLivingConversion(LivingConversionEvent.Pre event)
    {
        if (event.getEntityLiving() instanceof PiglinEntity)
            event.setCanceled(true);
    }

    public void onLivingConversion(LivingConversionEvent.Post event)
    {
        if (event.getEntityLiving() instanceof VillagerEntity)
            event.getEntityLiving().addPotionEffect(new EffectInstance(Effects.LUCK, 20));
    }
}
