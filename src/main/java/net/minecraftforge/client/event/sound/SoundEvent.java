package net.minecraftforge.client.event.sound;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;

public class SoundEvent extends Event
{
    public final SoundManager manager;
    public final SoundHandler handler;

    public SoundEvent(SoundManager manager, SoundHandler handler)
    {
        this.manager = manager;
        this.handler = handler;
    }

    public static class PositionedSoundEvent extends SoundEvent
    {
        public final ISound sound;
        public final float x;
        public final float y;
        public final float z;

        public PositionedSoundEvent(SoundManager manager, SoundHandler handler, ISound sound)
        {
            super(manager, handler);
            this.sound = sound;
            this.x = sound.getXPosF();
            this.y = sound.getYPosF();
            this.z = sound.getZPosF();
        }
    }

    public static class SoundSourceEvent extends PositionedSoundEvent
    {
        public final String uuid;

        public SoundSourceEvent(SoundManager manager, SoundHandler handler, ISound sound, String uuid)
        {
            super(manager, handler, sound);
            this.uuid = uuid;
        }
    }
}
