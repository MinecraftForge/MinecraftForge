package net.minecraftforge.common;

import net.minecraft.world.World;

//To be expanded for generic Mod fake players?
public class FakePlayerFactory
{
    // Map of all active fake player usernames to their entities
    private static java.util.Map<String, FakePlayer> fakePlayers = new java.util.HashMap<String, FakePlayer>();
    private static FakePlayer MINECRAFT_PLAYER = null;
    
    public static FakePlayer getMinecraft(World world)
    {
        if (MINECRAFT_PLAYER == null)
        {
            MINECRAFT_PLAYER = FakePlayerFactory.get(world,  "[Minecraft]");
        }
        return MINECRAFT_PLAYER;
    }
    
    /**
     * Get a fake player with a given username
     */
    public static FakePlayer get(World world, String username)
    {
        if (!fakePlayers.containsKey(username))
        {
            FakePlayer fakePlayer = new FakePlayer(world, username);
            fakePlayers.put(username, fakePlayer);
        }

        return fakePlayers.get(username);
    }
}
