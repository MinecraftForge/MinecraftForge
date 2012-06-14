package net.minecraft.src.forge.adaptors;

import net.minecraft.src.Entity;
import net.minecraft.src.SoundManager;
import net.minecraft.src.SoundPoolEntry;
import net.minecraft.src.forge.ISoundHandler;

/**
 * Adaptor class for convince sake when implementing ISoundHandler, as most 
 * mods will only actually care about onSetupAudio/onLoadSoundSettings.
 */
public class SoundHandlerAdaptor implements ISoundHandler 
{
    @Override
    public void onSetupAudio(SoundManager soundManager){}

    @Override
    public void onLoadSoundSettings(SoundManager soundManager){}

    @Override
    public SoundPoolEntry onPlayBackgroundMusic(SoundManager soundManager, SoundPoolEntry entry)
    {
        return entry;
    }

    @Override
    public SoundPoolEntry onPlayStreaming(SoundManager soundManager, SoundPoolEntry entry, String soundName, float x, float y, float z)
    {
        return entry;
    }

    @Override
    public SoundPoolEntry onPlaySound(SoundManager soundManager, SoundPoolEntry entry, String soundName, float x, float y, float z, float volume, float pitch)
    {
        return entry;
    }

    @Override
    public SoundPoolEntry onPlaySoundEffect(SoundManager soundManager, SoundPoolEntry entry, String soundName, float volume, float pitch)
    {
        return entry;
    }

    @Override
    public String onPlaySoundAtEntity(Entity entity, String soundName, float volume, float pitch)
    {
        return soundName;
    }
}
