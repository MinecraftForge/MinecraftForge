package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraftforge.client.event.sound.SoundEvent.PositionedSoundEvent;

/***
 * Raised when the SoundManager tries to play a normal sound,
 * 
 * If you return null from this function it will prevent the sound from being played,
 * you can return a different entry if you want to change the sound being played.
 */
public class PlaySoundEvent extends PositionedSoundEvent
{
    public final SoundCategory category;
    public final String name;
    public final float volume;
    public final float pitch;
    public ISound result;

    public PlaySoundEvent(SoundManager manager, SoundHandler handler, ISound sound, SoundCategory category)
    {
        super(manager, handler, sound);
        this.category = category;
        this.name = sound.getPositionedSoundLocation().getResourcePath();
        this.volume = sound.getVolume();
        this.pitch = sound.getPitch();
        this.result = sound;
    }
}
