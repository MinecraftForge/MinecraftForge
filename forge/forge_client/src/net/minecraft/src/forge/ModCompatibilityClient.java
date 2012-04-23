package net.minecraft.src.forge;

import java.io.File;
import java.io.IOException;

import paulscode.sound.SoundSystemConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public class ModCompatibilityClient 
{
    /**
     * Trys to get the class for the specified name, will also try the 
     * net.minecraft.src package in case we are in MCP
     * Returns null if not found.
     * 
     * @param name The class name
     * @return The Class, or null if not found
     */
    private static Class getClass(String name)
    {
        try 
        {
            return Class.forName(name);
        }
        catch (Exception e)
        {
            try
            {
                return Class.forName("net.minecraft.src." + name);
            }
            catch (Exception e2)
            {
                return null;
            }
        }
    }
    
    /************************************************************************************************
     * Risugami's AudioMod Compatibility
     * http://www.minecraftforum.net/topic/75440-
     * 
     * AudioMod adds a few extra codecs, loads audio from /resources/mods/*,
     * introduces the concept of 'cave' sounds, which are determined by if 
     * the player is underneath a solid block.
     * 
     * It also lowers the interval between background music songs to 6000
     */
    public static SoundPool audioModSoundPoolCave;
    private static int isAudioModInstalled = -1; 

    /**
     * Determine if AudioMod is installed by checking for the existence of IBMX Codec,
     * I wish there was a less ambiguous way, but there isn't. 
     * @return True if the IBMX codec was found (indicating AudioMod is installed)
     */
    public static boolean isAudioModInstalled()
    {
        if (isAudioModInstalled == -1)
        {
            isAudioModInstalled = (getClass("paulscode.sound.codecs.CodecIBXM") != null ? 1 : 0);
        }
        return isAudioModInstalled == 1;
    }
    
    /**
     * Populates the sound pools with with sounds from the /resources/mods folder
     * And sets the interval between background music to 6000
     * 
     * @param mngr The SoundManager instance
     */
    public static void audioModLoad(SoundManager mngr)
    {
        if (!isAudioModInstalled())
        {
            return;
        }
        audioModSoundPoolCave = new SoundPool();
        audioModLoadModAudio("minecraft/resources/mod/sound", mngr.getSoundsPool());
        audioModLoadModAudio("minecraft/resources/mod/streaming", mngr.getStreamingPool());
        audioModLoadModAudio("minecraft/resources/mod/music", mngr.getMusicPool());
        audioModLoadModAudio("minecraft/resources/mod/cavemusic", audioModSoundPoolCave);
        
        if (mngr.MUSIC_INTERVAL == 12000)
        {
            mngr.MUSIC_INTERVAL = 6000;
        }
    }
    
    /**
     * Walks the given path in the Minecraft app directory and adds audio to the SoundPool
     * @param path The path to walk
     * @param pool The pool to add sound to
     */
    private static void audioModLoadModAudio(String path, SoundPool pool)
    {
        File folder = Minecraft.getAppDir(path);

        try
        {
            audioModWalkFolder(folder, folder, pool);
        }
        catch (IOException ex)
        {
            ModLoader.getLogger().fine("Loading Mod audio failed for folder: " + path);
            ModLoader.getLogger().fine(ex.toString());
            ex.printStackTrace();
        }
    }
    
    /**
     * Walks the folder path recursively and calls pool.addSound on any file it finds.
     * 
     * @param base The base path for the folder, determines the name when calling addSound
     * @param folder The current folder
     * @param pool The SoundPool to add the sound to
     * @throws IOException
     */
    private static void audioModWalkFolder(File base, File folder, SoundPool pool) throws IOException
    {
        if (folder.exists() || folder.mkdirs())
        {
            for (File file : folder.listFiles())
            {             
                if (!file.getName().startsWith("."))
                {
                    if (file.isDirectory())
                    {
                        audioModWalkFolder(base, file, pool);
                    }
                    else if (file.isFile())
                    {
                        String subpath = file.getPath().substring(base.getPath().length() + 1).replace('\\', '/');
                        pool.addSound(subpath, file);
                    }
                }
            }
        }
    }

    /**
     * Adds the IBXM codec and associates it with .xm, .s3m, and .mod
     */
    public static void audioModAddCodecs() 
    {
        Class codec = getClass("paulscode.sound.codecs.CodecIBXM");
        if (isAudioModInstalled() && codec != null)
        {
            SoundSystemConfig.setCodec("xm", codec);
            SoundSystemConfig.setCodec("s3m", codec);
            SoundSystemConfig.setCodec("mod", codec);
        }
    }

    /**
     * If the current player is underground, it picks a random song from the cave sound pool,
     * if they are not it returns the passed in entry.
     * 
     * @param soundManager The SoundManager instance
     * @param current The currently selected entry
     * @return A soundPool entry to be played as the background music
     */
    public static SoundPoolEntry audioModPickBackgroundMusic(SoundManager soundManager, SoundPoolEntry current) 
    {
        Minecraft mc = ModLoader.getMinecraftInstance();
        if (isAudioModInstalled() && mc != null && mc.theWorld != null && audioModSoundPoolCave != null)
        {
            Entity ent = mc.renderViewEntity;
            int x = MathHelper.func_40346_b(ent.posX);
            int y = MathHelper.func_40346_b(ent.posY);
            int z = MathHelper.func_40346_b(ent.posZ);
            return (mc.theWorld.canBlockSeeTheSky(x, y, z) ? current : audioModSoundPoolCave.getRandomSound());
        }
        return current;
    }
}
