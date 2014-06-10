package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;

/***
 * Raised when the SoundManager tries to play a normal sound.
 * 
 * If you return null from this function it will prevent the sound from being played,
 * you can return a different entry if you want to change the sound being played.
 */
@Deprecated // Switch with PlaySoundEvent17 in 1.8
public class PlaySoundEvent extends SoundResultEvent
{
    public final float x;
    public final float y;
    public final float z;
    public PlaySoundEvent(SoundManager manager, SoundPoolEntry source, String name, float x, float y, float z, float volume, float pitch)
    { 
        super(manager, source, name, volume, pitch);
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
