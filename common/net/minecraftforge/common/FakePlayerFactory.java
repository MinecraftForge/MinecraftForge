package net.minecraftforge.common;

import net.minecraft.world.World;

//To be expanded for generic Mod fake players?
public class FakePlayerFactory
{
    private static FakePlayer MINECRAFT_PLAYER = null;
    
    public static FakePlayer getMinecraft(World world)
    {
        if (MINECRAFT_PLAYER == null)
        {
            MINECRAFT_PLAYER = new FakePlayer(world, "[Minecraft]");
        }
        return MINECRAFT_PLAYER;
    }
}
