package net.minecraftforge.client.event.sound;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraftforge.common.MinecraftForge;

public class SoundEvent extends Event
{
    public final SoundManager manager;
    public SoundEvent(SoundManager manager)
    {
        this.manager = manager;
    }
    
    @Deprecated
    public static SoundPoolEntry getResult(SoundResultEvent event)
    {
        MinecraftForge.EVENT_BUS.post(event);
        return event.result;
    }

    public static class SoundSourceEvent extends SoundEvent
    {
        public final ISound sound;
        public final String uuid;
        public final String name;

        public SoundSourceEvent(SoundManager manager, ISound sound, String uuid)
        {
            super(manager);
            this.name = sound.getPositionedSoundLocation().getResourcePath();
            this.sound = sound;
            this.uuid = uuid;
        }
    }
}