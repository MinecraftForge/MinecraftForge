package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;

public class PlaySoundSourceEvent extends SoundSourceEvent
{
    public PlaySoundSourceEvent(SoundManager manager, SoundHandler handler, ISound sound, String uuid)
    {
        super(manager, handler, sound, uuid);
    }
}
