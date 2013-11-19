package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;

public abstract class SoundResultEvent extends SoundEvent
{
    public final SoundManager manager;
    public final SoundPoolEntry source;
    public final String name;
    public final float volume;
    public final float pitch;
    public SoundPoolEntry result;
    
    public SoundResultEvent(SoundManager manager, SoundPoolEntry source, String name, float volume, float pitch)
    {
        this.manager = manager;
        this.source = source;
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
        this.result = source;
    }
}
