package net.minecraftforge.debug.world;

import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.event.ParticleWorldHandleEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ParticleWorldHandleEventTest.MODID)
@Mod(value = ParticleWorldHandleEventTest.MODID)
public class ParticleWorldHandleEventTest {

    public static final String MODID = "particle_world_handle_event_test";

    /**
     * The following will replace all {@link ParticleTypes#DAMAGE_INDICATOR} with {@link ParticleTypes#ANGRY_VILLAGER}
     * as well as raising the Y-coord with 2, and doubling the particle speed.
     * @param event
     */
    @SubscribeEvent
    public static void onEvent(ParticleWorldHandleEvent event) {
        if(event.getParticle().getParameters().equals(ParticleTypes.DAMAGE_INDICATOR.getParameters())) {
            event.setYCoord(event.getYCoord() + 2);
            event.setParticle(ParticleTypes.ANGRY_VILLAGER);
            event.setParticleSpeed(event.getParticleSpeed() * 2);
        }
    }
}
