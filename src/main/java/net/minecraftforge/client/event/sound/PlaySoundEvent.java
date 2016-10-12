package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.SoundCategory;

/***
 * Raised when the SoundManager tries to play a normal sound.
 *
 * If you return null from this function it will prevent the sound from being played,
 * you can return a different entry if you want to change the sound being played.
 */
public class PlaySoundEvent extends SoundEvent
{
    private final String name;
    private final ISound sound;
    private ISound result;

    public PlaySoundEvent(SoundManager manager, ISound sound)
    {
        super(manager);
        this.sound = sound;
        this.name = sound.getSoundLocation().getResourcePath();
        this.setResultSound(sound);
    }

    public String getName()
    {
        return name;
    }

    public ISound getSound()
    {
        return sound;
    }

    public ISound getResultSound()
    {
        return result;
    }

    public void setResultSound(ISound result)
    {
        this.result = result;
    }
}
