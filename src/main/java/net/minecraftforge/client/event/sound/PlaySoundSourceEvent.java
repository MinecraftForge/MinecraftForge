package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;

public class PlaySoundSourceEvent extends SoundSourceEvent
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
    public PlaySoundSourceEvent(SoundManager manager, String name, float x, float y, float z)
    {
        super(manager, null, null);
        this.manager = manager;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PlaySoundSourceEvent(SoundManager manager, ISound sound, String uuid)
    {
        super(manager, sound, uuid);
        this.name = sound.getPositionedSoundLocation().getResourcePath();        
        this.manager = manager;
        this.x = this.y = this.z = 0;
    }
}