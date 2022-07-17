package net.minecraftforge.event.entity.living;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "forge")
public class TestEvent {

    @SubscribeEvent
    public static void livingGravity(LivingGravityEvent event) {
        event.setGravity(0.04); //If gravity attribute not set to 0.08 this event get called
    }
}
