package net.minecraftforge.client.event.sound;

import net.minecraft.src.SoundManager;

public class PlaySoundEffectSourceEvent extends SoundEvent
{
    public final SoundManager manager;
    public final String name;
    public PlaySoundEffectSourceEvent(SoundManager manager, String name)
    {
        this.manager = manager;
        this.name = name;
    }
}
