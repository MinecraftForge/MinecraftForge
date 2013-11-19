package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;

/**
 * Raised when the SoundManager tries to play a Background Music file,
 * If you return null from this function it will prevent the sound from being played,
 * you can return a different entry if you want to change the sound being played.
 */
public class PlayBackgroundMusicEvent extends SoundResultEvent
{
    public PlayBackgroundMusicEvent(SoundManager manager, SoundPoolEntry entry)
    { super(manager, entry, null, 0.0f, 0.0f); }
}