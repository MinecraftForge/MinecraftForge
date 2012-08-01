package net.minecraft.src.forge.adaptors;

import paulscode.sound.SoundSystem;
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

    @Override
    public void playStreamingSource(SoundSystem soundSystem, String sourceName, float x, float y, float z){}

    @Override
    public void playSoundSource(SoundSystem soundSystem, String sourceName, float x, float y, float z){}

    @Override
    public void playSoundEffectSource(SoundSystem soundSystem, String sourceName){}

    @Override
    public void onSetListener(SoundManager soundManager, float elapsed, float posX, float posY, float posZ, float lookX, float lookY, float lookZ){}
}
