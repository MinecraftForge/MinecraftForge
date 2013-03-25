package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundManager;

public class PlaySoundSourceEvent extends SoundEvent
{
    public final SoundManager manager;
    public final String name;
    public final float x;
    public final float y;
    public final float z;
    public PlaySoundSourceEvent(SoundManager manager, String name, float x, float y, float z)
    {
        this.manager = manager;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}