package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundManager;

@Deprecated //Remove in 1.8
public class PlaySoundEffectSourceEvent extends SoundEvent
{
    public final SoundManager manager;
    public final String name;
    public PlaySoundEffectSourceEvent(SoundManager manager, String name)
    {
        super(manager);
        this.manager = manager;
        this.name = name;
    }
}
