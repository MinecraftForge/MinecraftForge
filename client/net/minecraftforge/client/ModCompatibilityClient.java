package net.minecraftforge.client;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;

import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecIBXM;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ModCompatibilityClient
{
    /**
     * Tries to get the class for the specified name, will also try the
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

    /**
     * Populates the sound pools with with sounds from the /resources/mods folder
     * And sets the interval between background music to 6000
     *
     * @param mngr The SoundManager instance
     */
    public static void audioModLoad(SoundManager mngr)
    {
        audioModSoundPoolCave = new SoundPool();
        audioModLoadModAudio("resources/mod/sound", mngr.soundPoolSounds);
        audioModLoadModAudio("resources/mod/streaming", mngr.soundPoolStreaming);
        audioModLoadModAudio("resources/mod/music", mngr.soundPoolMusic);
        audioModLoadModAudio("resources/mod/cavemusic", audioModSoundPoolCave);

        if (mngr.MUSIC_INTERVAL == 12000)
        {
            mngr.MUSIC_INTERVAL = 6000;
        }
    }

    /**
     * Walks the given path in the Minecraft app directory and adds audio to the SoundPool
     * 
     * @param path The path to walk
     * @param pool The pool to add sound to
     */
    private static void audioModLoadModAudio(String path, SoundPool pool)
    {
        File folder = new File(Minecraft.getMinecraftDir(), path);

        try
        {
            audioModWalkFolder(folder, folder, pool);
        }
        catch (IOException ex)
        {
            FMLLog.log(Level.FINE, ex, "Loading Mod audio failed for folder: %s", path);
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
        SoundSystemConfig.setCodec("xm",  CodecIBXM.class);
        SoundSystemConfig.setCodec("s3m", CodecIBXM.class);
        SoundSystemConfig.setCodec("mod", CodecIBXM.class);
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
        Minecraft mc = FMLClientHandler.instance().getClient();
        if (mc != null && mc.theWorld != null && audioModSoundPoolCave != null)
        {
            Entity ent = mc.renderViewEntity;
            int x = MathHelper.truncateDoubleToInt(ent.posX);
            int y = MathHelper.truncateDoubleToInt(ent.posY);
            int z = MathHelper.truncateDoubleToInt(ent.posZ);
            return (mc.theWorld.canBlockSeeTheSky(x, y, z) ? current : audioModSoundPoolCave.getRandomSound());
        }
        return current;
    }

    /***********************************************************************************************************
     * SDK's ModLoaderMP
     * http://www.minecraftforum.net/topic/86765-
     *
     * ModLoaderMP was supposed to be a reliable server side version of ModLoader, however it has
     * gotten the reputation of being really slow to update. Never having bugfixes, breaking compatibility
     * with the client side ModLoader.
     *
     * So we have replaced it with our own system called FML (Forge ModLoader)
     * it is a stand alone mod, that Forge relies on, and that is open source/community driven.
     * https://github.com/cpw/FML
     *
     * However, for compatibilities sake, we provide the ModLoaderMP's hooks so that the end user
     * does not need to make a choice between the two on the client side.
     **/
    private static int isMLMPInstalled = -1;

    /**
     * Determine if ModLoaderMP is installed by checking for the existence of the BaseModMp class.
     * @return True if BaseModMp was installed (indicating the existance of MLMP)
     */
    public static boolean isMLMPInstalled()
    {
        if (isMLMPInstalled == -1)
        {
            isMLMPInstalled = (getClass("ModLoaderMp") != null ? 1 : 0);
        }
        return isMLMPInstalled == 1;
    }

    /**
     * Attempts to spawn a vehicle using ModLoaderMP's vehicle spawn registry, if MLMP is not installed
     * it returns the passed in currentEntity
     *
     * @param type The Type ID of the vehicle
     * @param world The current world
     * @param x The spawn X position
     * @param y The spawn Y position
     * @param z The spawn Z position
     * @param thrower The entity that spawned the vehicle {possibly null}
     * @param currentEntity The current value to return if MLMP is not installed
     * @return The new spawned entity
     * @throws Exception
     */
    public static Object mlmpVehicleSpawn(int type, World world, double x, double y, double z, Entity thrower, Object currentEntity) throws Exception
    {
        Class mlmp = getClass("ModLoaderMp");
        if (!isMLMPInstalled() || mlmp == null)
        {
            return currentEntity;
        }

        Object entry = mlmp.getDeclaredMethod("handleNetClientHandlerEntities", int.class).invoke(null, type);
        if (entry == null)
        {
            return currentEntity;
        }

        Class entityClass = (Class)entry.getClass().getDeclaredField("entityClass").get(entry);
        Object ret = (Entity)entityClass.getConstructor(World.class, Double.TYPE, Double.TYPE, Double.TYPE).newInstance(world, x, y, z);

        if (entry.getClass().getDeclaredField("entityHasOwner").getBoolean(entry))
        {
            Field owner = entityClass.getField("owner");

            if (!Entity.class.isAssignableFrom(owner.getType()))
            {
                throw new Exception(String.format("Entity\'s owner field must be of type Entity, but it is of type %s.", owner.getType()));
            }

            if (thrower == null)
            {
                System.out.println("Received spawn packet for entity with owner, but owner was not found.");
                FMLLog.fine("Received spawn packet for entity with owner, but owner was not found.");
            }
            else
            {
                if (!owner.getType().isAssignableFrom(thrower.getClass()))
                {
                    throw new Exception(String.format("Tried to assign an entity of type %s to entity owner, which is of type %s.", thrower.getClass(), owner.getType()));
                }

                owner.set(ret, thrower);
            }
        }
        return ret;
    }

    /**
     * Attempts to invoke ModLoaderMp.handleGUI if ModLoaderMP is installed.
     * If not, it does nothing
     *
     * @param pkt The open window packet
     */
    public static void mlmpOpenWindow(Packet100OpenWindow pkt)
    {
        Class mlmp = getClass("ModLoaderMp");
        if (!isMLMPInstalled() || mlmp == null)
        {
            return;
        }

        try
        {
            mlmp.getDeclaredMethod("handleGUI", Packet100OpenWindow.class).invoke(null, pkt);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
