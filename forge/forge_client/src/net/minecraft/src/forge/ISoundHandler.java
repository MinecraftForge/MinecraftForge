package net.minecraft.src.forge;

import net.minecraft.src.Entity;
import net.minecraft.src.SoundManager;
import net.minecraft.src.SoundPoolEntry;

public interface ISoundHandler 
{
    /**
     * This event is raised by the SoundManager when it does its first setup of the 
     * SoundSystemConfig's codecs, use this function to add your own codecs.
     * @param soundManager The SoundManager instance
     */
    void onSetupAudio(SoundManager soundManager);

    /**
     * Raised by the SoundManager.loadSoundSettings, this would be a good place for 
     * adding your custom sounds to the SoundPool.
     * 
     * @param soundManager The SoundManager instance
     */
    void onLoadSoundSettings(SoundManager soundManager);

    /**
     * Raised when the SoundManager tries to play a Background Music file,
     * If you return null from this function it will prevent the sound from being played,
     * you can return a different entry if you want to change the sound being played.
     * 
     * If you do not want to change anything, just return the passed in entry.
     * 
     * @param soundManager The SoundManager instance
     * @param entry The current entry that will be played
     * @return The new sound entry to play, or the current one passed in.
     */
    SoundPoolEntry onPlayBackgroundMusic(SoundManager soundManager, SoundPoolEntry entry);

    /**
     * Raised when the SoundManager tries to play a 'Streaming' file,
     * in vanilla it is only the Jukebox that uses this function.
     * 
     * If you return null from this function it will prevent the sound from being played,
     * you can return a different entry if you want to change the sound being played.
     * 
     * If you do not want to change anything, just return the passed in entry.
     * 
     * @param soundManager The SoundManager instance
     * @param entry The current entry that will be played
     * @param soundName The name of the request sound
     * @param x The X position where the sound will be played
     * @param y The Y position where the sound will be played
     * @param z The Z position where the sound will be played
     * @return The new sound entry to play, or the current one passed in.
     */
    SoundPoolEntry onPlayStreaming(SoundManager soundManager, SoundPoolEntry entry, String soundName, float x, float y, float z);

    /***
     * Raised when the SoundManager tries to play a normal sound,
     * dogs barking, footsteps, etc. THe majority of all sounds during normal game play.
     * 
     * If you return null from this function it will prevent the sound from being played,
     * you can return a different entry if you want to change the sound being played.
     * 
     * If you do not want to change anything, just return the passed in entry. 
     * 
     * @param soundManager The SoundManager instance
     * @param entry The current entry that will be played
     * @param soundName The name of the request sound
     * @param x The X position where the sound will be played
     * @param y The Y position where the sound will be played
     * @param z The Z position where the sound will be played
     * @param volume The sound's volume, between 0.0 and 1.0
     * @param pitch The sound's pitch
     * @return The new sound entry to play, or the current one passed in.
     */
    SoundPoolEntry onPlaySound(SoundManager soundManager, SoundPoolEntry entry, String soundName, float x, float y, float z, float volume, float pitch);

    /**
     * Raised when the SoundManager tries to play a effect sound,
     * currently the only known sounds are 'random.click' when a GUI button is clicked,
     * or 'portal.trigger' and 'portal.travel' when the player is near/inside a portal.
     * 
     * If you return null from this function it will prevent the sound from being played,
     * you can return a different entry if you want to change the sound being played.
     * 
     * If you do not want to change anything, just return the passed in entry. 
     * 
     * @param soundManager The SoundManager instance
     * @param entry The current entry that will be played
     * @param soundName The name of the request sound
     * @param volume The sound's volume, between 0.0 and 1.0
     * @param pitch The sound's pitch
     * @return The new sound entry to play, or the current one passed in.
     */
    SoundPoolEntry onPlaySoundEffect(SoundManager soundManager, SoundPoolEntry entry, String soundName, float volume, float pitch);
    
    /**
     * Raised when an entity attempts to play a sound via World's playSoundAtEntity
     * function. This is so that additional data about the state of the entity can
     * be obtained efficiently without using AABBs. An important thing to note:
     * This hook will be called even if the sound is off.
     * 
     * @param entity The entity that the sound should be played at. Always the calling Entity or the Player.
     * @param soundName The name of the requested sound.
     * @param volume The sound's volume, between 0.0 and 1.0.
     * @param pitch The sound's pitch    
     * @return The sound to play, null to cancel the event.
     */
    String onPlaySoundAtEntity(Entity entity, String soundName, float volume, float pitch);
}
