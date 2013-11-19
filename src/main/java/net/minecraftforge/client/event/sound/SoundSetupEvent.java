package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundManager;

/**
 * This event is raised by the SoundManager when it does its first setup of the 
 * SoundSystemConfig's codecs, use this function to add your own codecs.
 */
public class SoundSetupEvent extends SoundEvent
{
    public final SoundManager manager;
    public SoundSetupEvent(SoundManager manager)
    {
        this.manager = manager;
    }
}
