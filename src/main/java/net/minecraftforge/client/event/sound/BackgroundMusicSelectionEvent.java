package net.minecraftforge.client.event.sound;

import javax.annotation.Nullable;

import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SimpleSound;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired whenever {@link MusicTicker} tries to get the
 * {@link BackgroundMusicSelector} for the current situation.
 * 
 * Used to set the background music being played.
 * */
public class BackgroundMusicSelectionEvent extends Event
{
    @Nullable
    private final ISound currentMusic;
    private final BackgroundMusicSelector defaultSelector;
    private BackgroundMusicSelector newSelector;

    public BackgroundMusicSelectionEvent(@Nullable ISound currentMusic, BackgroundMusicSelector defaultSelector)
    {
        this.currentMusic = currentMusic;
        this.defaultSelector = defaultSelector;
        this.newSelector = defaultSelector;
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
     * Gets the vanilla selector for the scenario.
     * */
    public BackgroundMusicSelector getDefaultSelector()
    {
        return this.defaultSelector;
    }

    /**
     * Gets the final selector. Uses the default if not set.
     * */
    public BackgroundMusicSelector getBackgroundMusicSelector()
    {
        return this.newSelector;
    }

    /**
     * Sets the new selector to use for the background music.
     * */
    public void setBackgroundMusicSelector(final BackgroundMusicSelector selector)
    {
        this.newSelector = selector;
    }
}
