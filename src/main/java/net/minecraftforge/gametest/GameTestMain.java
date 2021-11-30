package net.minecraftforge.gametest;

import net.minecraft.server.Main;

public class GameTestMain
{
    public static void main(String[] args)
    {
        System.setProperty("forge.enablegametest", "true");
        Main.main(args);
    }
}
