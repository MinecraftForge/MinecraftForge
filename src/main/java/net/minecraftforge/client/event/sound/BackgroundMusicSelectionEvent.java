/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
