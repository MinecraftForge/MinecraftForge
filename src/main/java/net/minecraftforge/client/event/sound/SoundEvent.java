package net.minecraftforge.client.event.sound;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;

public class SoundEvent extends Event
{
    private final SoundManager manager;
    public SoundEvent(SoundManager manager)
    {
        this.manager = manager;
    }

    public SoundManager getManager()
    {
        return manager;
    }

    public static class SoundSourceEvent extends SoundEvent
    {
        private final ISound sound;
        private final String uuid;
        private final String name;

        public SoundSourceEvent(SoundManager manager, ISound sound, String uuid)
        {
            super(manager);
            this.name = sound.getSoundLocation().getResourcePath();
            this.sound = sound;
            this.uuid = uuid;
        }

        public ISound getSound()
        {
            return sound;
        }

        public String getUuid()
        {
            return uuid;
        }

        public String getName()
        {
            return name;
        }
    }
}
