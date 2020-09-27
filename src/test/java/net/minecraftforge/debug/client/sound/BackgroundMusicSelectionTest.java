package net.minecraftforge.debug.client.sound;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.BackgroundMusicSelectionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("background_music_selection_test")
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class BackgroundMusicSelectionTest
{
    static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onMusicSelection(final BackgroundMusicSelectionEvent event)
    {
        //Checks values to make sure the selection event is working
        LOGGER.debug("Current Sound: {}, Selector: {}", event.getCurrentMusic(), event.getDefaultSelector());
        //Sets the music to always play the credits sequence
        event.setBackgroundMusicSelector(BackgroundMusicTracks.field_232672_c_);
    }
}
