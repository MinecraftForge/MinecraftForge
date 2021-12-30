package net.minecraftforge.debug.misc;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("play_sound_at_entity_test")
public class PlaySoundAtEntityEventTest {
    private static final boolean ENABLED = true;
    private static final Logger LOGGER = LogManager.getLogger("Sound Event Test Test");
    public PlaySoundAtEntityEventTest() {
        if (ENABLED) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onSoundEvent(PlaySoundAtEntityEvent soundEvent) {
        LOGGER.info(String.format("GOT %S SOUND EVENT at {%f, %f, %f} in %s",
                soundEvent.getSound().getRegistryName().getPath(),
                soundEvent.getX(),
                soundEvent.getY(),
                soundEvent.getZ(),
                soundEvent.getLevel().dimension().location().getPath()
        ));
    }
}
