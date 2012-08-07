package net.minecraftforge.client.event.sound;

import net.minecraft.src.SoundManager;
import net.minecraft.src.SoundPoolEntry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

public class SoundEvent extends Event
{
    public SoundEvent(){}
    
    public static SoundPoolEntry getResult(SoundResultEvent event)
    {
        MinecraftForge.EVENT_BUS.post(event);
        return event.result;
    }
    
    public static abstract class SoundResultEvent extends SoundEvent
    {
        public final SoundManager manager;
        public final SoundPoolEntry source;
        public final String name;
        public final float volume;
        public final float pitch;
        public SoundPoolEntry result;
        
        public SoundResultEvent(SoundManager manager, SoundPoolEntry source, String name, float volume, float pitch)
        {
            this.manager = manager;
            this.source = source;
            this.name = name;
            this.volume = volume;
            this.pitch = pitch;
            this.result = source;
        }
    }
    
    /**
     * Raised by the SoundManager.loadSoundSettings, this would be a good place for 
     * adding your custom sounds to the SoundPool.
     */
    public static class SoundLoadEvent extends SoundEvent
    {
        public final SoundManager manager;
        public SoundLoadEvent(SoundManager manager)
        {
            this.manager = manager;
        }
    }
    
    /**
     * This event is raised by the SoundManager when it does its first setup of the 
     * SoundSystemConfig's codecs, use this function to add your own codecs.
     */
    public static class SoundSetupEvent extends SoundEvent
    {
        public final SoundManager manager;
        public SoundSetupEvent(SoundManager manager)
        {
            this.manager = manager;
        }
    }
    
    /**
     * Raised when the SoundManager tries to play a Background Music file,
     * If you return null from this function it will prevent the sound from being played,
     * you can return a different entry if you want to change the sound being played.
     */
    public static class PlayBackgroundMusicEvent extends SoundResultEvent
    {
        public PlayBackgroundMusicEvent(SoundManager manager, SoundPoolEntry entry)
        { super(manager, entry, null, 0.0f, 0.0f); }
    }
    
    /**
     * Raised when the SoundManager tries to play a 'Streaming' file,
     * in vanilla it is only the Jukebox that uses this function.
     * 
     * If you return null from this function it will prevent the sound from being played,
     * you can return a different entry if you want to change the sound being played.
     * 
     */
    public static class PlayStreamingEvent extends SoundResultEvent
    {
        public final float x;
        public final float y;
        public final float z;
        public PlayStreamingEvent(SoundManager manager, SoundPoolEntry source, String name, float x, float y, float z)
        { 
            super(manager, source, name, 0.0f, 0.0f);
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    /***
     * Raised when the SoundManager tries to play a normal sound,
     * dogs barking, footsteps, etc. THe majority of all sounds during normal game play.
     * 
     * If you return null from this function it will prevent the sound from being played,
     * you can return a different entry if you want to change the sound being played.
     */
    public static class PlaySoundEvent extends SoundResultEvent
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
    public static class PlaySoundEffectEvent extends SoundResultEvent
    {
        public PlaySoundEffectEvent(SoundManager manager, SoundPoolEntry source, String name, float volume, float pitch)
        { super(manager, source, name, volume, pitch); }
    }
}