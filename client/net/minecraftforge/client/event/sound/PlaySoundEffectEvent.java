package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;

public class PlaySoundEffectEvent extends SoundResultEvent
{
    public PlaySoundEffectEvent(SoundManager manager, SoundPoolEntry source, String name, float volume, float pitch)
    { super(manager, source, name, volume, pitch); }
}
