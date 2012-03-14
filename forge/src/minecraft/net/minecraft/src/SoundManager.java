package net.minecraft.src;

import java.io.File;
import java.util.Random;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager
{
    /** A reference to the sound system. */
    private static SoundSystem sndSystem;

    /** Sound pool containing sounds. */
    private SoundPool soundPoolSounds = new SoundPool();

    /** Sound pool containing streaming audio. */
    private SoundPool soundPoolStreaming = new SoundPool();

    /** Sound pool containing music. */
    private SoundPool soundPoolMusic = new SoundPool();

    /**
     * The last ID used when a sound is played, passed into SoundSystem to give active sounds a unique ID
     */
    private int latestSoundID = 0;

    /** A reference to the game settings. */
    private GameSettings options;

    /** Set to true when the SoundManager has been initialised. */
    private static boolean loaded = false;

    /** RNG. */
    private Random rand = new Random();
    private int ticksBeforeMusic;

    public SoundManager()
    {
        this.ticksBeforeMusic = this.rand.nextInt(12000);
    }

    /**
     * Used for loading sound settings from GameSettings
     */
    public void loadSoundSettings(GameSettings par1GameSettings)
    {
        this.soundPoolStreaming.isGetRandomSound = false;
        this.options = par1GameSettings;

        if (!loaded && (par1GameSettings == null || par1GameSettings.soundVolume != 0.0F || par1GameSettings.musicVolume != 0.0F))
        {
            this.tryToSetLibraryAndCodecs();
        }
    }

    /**
     * Tries to add the paulscode library and the relevant codecs. If it fails, the volumes (sound and music) will be
     * set to zero in the options file.
     */
    private void tryToSetLibraryAndCodecs()
    {
        try
        {
            float var1 = this.options.soundVolume;
            float var2 = this.options.musicVolume;
            this.options.soundVolume = 0.0F;
            this.options.musicVolume = 0.0F;
            this.options.saveOptions();
            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
            SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
            SoundSystemConfig.setCodec("mus", CodecMus.class);
            SoundSystemConfig.setCodec("wav", CodecWav.class);
            sndSystem = new SoundSystem();
            this.options.soundVolume = var1;
            this.options.musicVolume = var2;
            this.options.saveOptions();
        }
        catch (Throwable var3)
        {
            var3.printStackTrace();
            System.err.println("error linking with the LibraryJavaSound plug-in");
        }

        loaded = true;
    }

    /**
     * Called when one of the sound level options has changed.
     */
    public void onSoundOptionsChanged()
    {
        if (!loaded && (this.options.soundVolume != 0.0F || this.options.musicVolume != 0.0F))
        {
            this.tryToSetLibraryAndCodecs();
        }

        if (loaded)
        {
            if (this.options.musicVolume == 0.0F)
            {
                sndSystem.stop("BgMusic");
            }
            else
            {
                sndSystem.setVolume("BgMusic", this.options.musicVolume);
            }
        }
    }

    /**
     * Called when Minecraft is closing down.
     */
    public void closeMinecraft()
    {
        if (loaded)
        {
            sndSystem.cleanup();
        }
    }

    /**
     * Adds a sounds with the name from the file. Args: name, file
     */
    public void addSound(String par1Str, File par2File)
    {
        this.soundPoolSounds.addSound(par1Str, par2File);
    }

    /**
     * Adds an audio file to the streaming SoundPool.
     */
    public void addStreaming(String par1Str, File par2File)
    {
        this.soundPoolStreaming.addSound(par1Str, par2File);
    }

    /**
     * Adds an audio file to the music SoundPool.
     */
    public void addMusic(String par1Str, File par2File)
    {
        this.soundPoolMusic.addSound(par1Str, par2File);
    }

    /**
     * If its time to play new music it starts it up.
     */
    public void playRandomMusicIfReady()
    {
        if (loaded && this.options.musicVolume != 0.0F)
        {
            if (!sndSystem.playing("BgMusic") && !sndSystem.playing("streaming"))
            {
                if (this.ticksBeforeMusic > 0)
                {
                    --this.ticksBeforeMusic;
                    return;
                }

                SoundPoolEntry var1 = this.soundPoolMusic.getRandomSound();

                if (var1 != null)
                {
                    this.ticksBeforeMusic = this.rand.nextInt(12000) + 12000;
                    sndSystem.backgroundMusic("BgMusic", var1.soundUrl, var1.soundName, false);
                    sndSystem.setVolume("BgMusic", this.options.musicVolume);
                    sndSystem.play("BgMusic");
                }
            }
        }
    }

    /**
     * Sets the listener of sounds
     */
    public void setListener(EntityLiving par1EntityLiving, float par2)
    {
        if (loaded && this.options.soundVolume != 0.0F)
        {
            if (par1EntityLiving != null)
            {
                float var3 = par1EntityLiving.prevRotationYaw + (par1EntityLiving.rotationYaw - par1EntityLiving.prevRotationYaw) * par2;
                double var4 = par1EntityLiving.prevPosX + (par1EntityLiving.posX - par1EntityLiving.prevPosX) * (double)par2;
                double var6 = par1EntityLiving.prevPosY + (par1EntityLiving.posY - par1EntityLiving.prevPosY) * (double)par2;
                double var8 = par1EntityLiving.prevPosZ + (par1EntityLiving.posZ - par1EntityLiving.prevPosZ) * (double)par2;
                float var10 = MathHelper.cos(-var3 * 0.017453292F - (float)Math.PI);
                float var11 = MathHelper.sin(-var3 * 0.017453292F - (float)Math.PI);
                float var12 = -var11;
                float var13 = 0.0F;
                float var14 = -var10;
                float var15 = 0.0F;
                float var16 = 1.0F;
                float var17 = 0.0F;
                sndSystem.setListenerPosition((float)var4, (float)var6, (float)var8);
                sndSystem.setListenerOrientation(var12, var13, var14, var15, var16, var17);
            }
        }
    }

    public void playStreaming(String par1Str, float par2, float par3, float par4, float par5, float par6)
    {
        if (loaded && this.options.soundVolume != 0.0F)
        {
            String var7 = "streaming";

            if (sndSystem.playing("streaming"))
            {
                sndSystem.stop("streaming");
            }

            if (par1Str != null)
            {
                SoundPoolEntry var8 = this.soundPoolStreaming.getRandomSoundFromSoundPool(par1Str);

                if (var8 != null && par5 > 0.0F)
                {
                    if (sndSystem.playing("BgMusic"))
                    {
                        sndSystem.stop("BgMusic");
                    }

                    float var9 = 16.0F;
                    sndSystem.newStreamingSource(true, var7, var8.soundUrl, var8.soundName, false, par2, par3, par4, 2, var9 * 4.0F);
                    sndSystem.setVolume(var7, 0.5F * this.options.soundVolume);
                    sndSystem.play(var7);
                }
            }
        }
    }

    /**
     * Plays a sound. Args: soundName, x, y, z, volume, pitch
     */
    public void playSound(String par1Str, float par2, float par3, float par4, float par5, float par6)
    {
        if (loaded && this.options.soundVolume != 0.0F)
        {
            SoundPoolEntry var7 = this.soundPoolSounds.getRandomSoundFromSoundPool(par1Str);

            if (var7 != null && par5 > 0.0F)
            {
                this.latestSoundID = (this.latestSoundID + 1) % 256;
                String var8 = "sound_" + this.latestSoundID;
                float var9 = 16.0F;

                if (par5 > 1.0F)
                {
                    var9 *= par5;
                }

                sndSystem.newSource(par5 > 1.0F, var8, var7.soundUrl, var7.soundName, false, par2, par3, par4, 2, var9);
                sndSystem.setPitch(var8, par6);

                if (par5 > 1.0F)
                {
                    par5 = 1.0F;
                }

                sndSystem.setVolume(var8, par5 * this.options.soundVolume);
                sndSystem.play(var8);
            }
        }
    }

    /**
     * Plays a sound effect with the volume and pitch of the parameters passed. The sound isn't affected by position of
     * the player (full volume and center balanced)
     */
    public void playSoundFX(String par1Str, float par2, float par3)
    {
        if (loaded && this.options.soundVolume != 0.0F)
        {
            SoundPoolEntry var4 = this.soundPoolSounds.getRandomSoundFromSoundPool(par1Str);

            if (var4 != null)
            {
                this.latestSoundID = (this.latestSoundID + 1) % 256;
                String var5 = "sound_" + this.latestSoundID;
                sndSystem.newSource(false, var5, var4.soundUrl, var4.soundName, false, 0.0F, 0.0F, 0.0F, 0, 0.0F);

                if (par2 > 1.0F)
                {
                    par2 = 1.0F;
                }

                par2 *= 0.25F;
                sndSystem.setPitch(var5, par3);
                sndSystem.setVolume(var5, par2 * this.options.soundVolume);
                sndSystem.play(var5);
            }
        }
    }
}
