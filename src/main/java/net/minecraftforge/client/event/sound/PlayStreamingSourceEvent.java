package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;

public class PlayStreamingSourceEvent extends SoundSourceEvent
{
    @Deprecated
    public final SoundManager manager;
    @Deprecated
    public final String name;
    @Deprecated
    public final float x;
    @Deprecated
    public final float y;
    @Deprecated
    public final float z;

    @Deprecated
    public PlayStreamingSourceEvent(SoundManager manager, String name, float x, float y, float z)
    {
        super(manager, null, null);
        this.manager = manager;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PlayStreamingSourceEvent(SoundManager manager, ISound sound, String uuid)
    {
        super(manager, sound, uuid);
        this.x = this.y = this.z = 0;
        this.manager = null;
        this.name = sound.getPositionedSoundLocation().getResourcePath();
    }
}