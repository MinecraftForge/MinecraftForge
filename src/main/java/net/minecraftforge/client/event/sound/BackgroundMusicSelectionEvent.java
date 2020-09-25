package net.minecraftforge.client.event.sound;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SimpleSound;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired whenever {@link MusicTicker} tries to get the current
 * {@link BackgroundMusicSelector} for the current situation.
 * 
 * When the setter below is called, the current background music selector
 * is replaced with the user set one. The selector will wait until the current
 * music is finished playing followed by a random delay specified by the new
 * selector or if {@link BackgroundMusicSelector#shouldReplaceCurrentMusic}
 * on the new selector is true.
 * 
 * If the setter is never called, the background music selector will default to
 * the client call from {@link Minecraft#getBackgroundMusicSelector}.
 * */
public class BackgroundMusicSelectionEvent extends Event
{
    private final MusicTicker musicTicker;
    private final Minecraft mc;
    @Nullable
    private final ISound currentMusic;
    private final int timeUntilNextMusic;
    private final BackgroundMusicSelector defaultSelector;
    private BackgroundMusicSelector newSelector;

    public BackgroundMusicSelectionEvent(MusicTicker musicTicker, Minecraft mc, @Nullable ISound currentMusic, int timeUntilNextMusic, BackgroundMusicSelector _default)
    {
        this.musicTicker = musicTicker;
        this.mc = mc;
        this.currentMusic = currentMusic;
        this.timeUntilNextMusic = timeUntilNextMusic;
        this.defaultSelector = _default;
    }

    /**Gets the music ticker that handled background music.*/
    public MusicTicker getMusicTicker()
    {
        return this.musicTicker;
    }

    /**Gets the instance of Minecraft being utilized.*/
    public Minecraft getMinecraft()
    {
        return this.mc;
    }

    /**
     * Gets the current sound being played.
     * Usually an instance of {@link SimpleSound}.
     * */
    public ISound getCurrentMusic()
    {
        return this.currentMusic;
    }

    /**
     * Gets the time until the next sound is played.
     * This will not be checked until the current music
     * has finished playing or if the selector instantly
     * replaces the current music being played.
     * */
    public int getTimeUtilNextMusic()
    {
        return this.timeUntilNextMusic;
    }

    /**Gets the default selector if the setter is not called.*/
    public BackgroundMusicSelector getCurrentBackgroundMusicSelector()
    {
        return this.defaultSelector;
    }

    /**Sets the new selector to use for the background music.*/
    public void setBackgroundMusicSelector(final BackgroundMusicSelector selector)
    {
        this.newSelector = selector;
    }

    /**Gets the new selector to use for the background music if present.*/
    @Nullable
    public BackgroundMusicSelector getNewBackgroundMusicSelector()
    {
        return newSelector;
    }
}
