package net.minecraftforge.debug;

import net.minecraft.entity.monster.StrayEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingBurnBySunlightEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("living_burn_by_sunlight_event_test")
public class LivingBurnBySunlightEventTest
{
    public LivingBurnBySunlightEventTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingBurnBySunlight);
    }

    public void onLivingBurnBySunlight(LivingBurnBySunlightEvent event)
    {
        if (event.getEntityLiving() instanceof StrayEntity)
            event.setDuration(0);
    }
}
