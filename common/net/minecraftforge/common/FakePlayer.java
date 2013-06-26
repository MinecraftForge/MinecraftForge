package net.minecraftforge.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class FakePlayer
{
    // Map of all active fake player usernames to their entities
    private static Map<String, EntityPlayerMP> fakePlayers = new HashMap<String, EntityPlayerMP>();

    /**
     * Get a fake player with a given username
     */
    public static EntityPlayerMP get(World world, String username)
    {
        if (!fakePlayers.containsKey(username))
        {
            EntityPlayerMP fakePlayer = new EntityPlayerMP(FMLCommonHandler.instance().getMinecraftServerInstance(), world, username, new ItemInWorldManager(world));
            fakePlayers.put(username, fakePlayer);
        }

        return fakePlayers.get(username);
    }
}