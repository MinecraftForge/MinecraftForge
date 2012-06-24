package net.minecraft.src;

import java.util.Set;

import net.minecraft.src.forge.ForgeHooks;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.NetworkMod;

/**
 * This class is just here to make the Forge version show up nicely in the ModLoader logs/Crash Screen
 */
public class mod_MinecraftForge extends NetworkMod
{
    @MLProp(info = "Set to true to disable darkrooms, it adds a extra check when planting the grass and plants to check that they won't instantly die.")
    public static boolean DISABLE_DARK_ROOMS = false;

    @MLProp(info = "Set to false to reproduce a vinella bug that prevents mobs from spawning on inverted half-slabs and inverted stairs.")
    public static boolean SPAWNER_ALLOW_ON_INVERTED = true;
    
    @MLProp(info = "The kick message used when a client tries to connect but does not have Minecraft Forge installed.")
    public static String NO_FORGE_KICK_MESSAGE = "This server requires you to have Minecraft Forge installed. http://MinecraftForge.net/";

    @MLProp(info = "Set to true to randomly shuffle the potential chunks for spawning, this is useful in FTB challange maps where you don't want one side hogging the spawns")
    public static boolean SPAWNER_MAKE_MORE_RANDOM = false;
    
    @Override
    public String getVersion()
    {
        return String.format("%d.%d.%d.%d",
                ForgeHooks.majorVersion,    ForgeHooks.minorVersion,
                ForgeHooks.revisionVersion, ForgeHooks.buildVersion);
    }

    @Override
    public void load()
    {
        MinecraftForge.getDungeonLootTries(); //Random thing to make things Initialize
        int x = 0;
        for (BaseMod mod : ModLoader.getLoadedMods())
        {
            if (mod instanceof NetworkMod)
            {
                if (x == Item.map.shiftedIndex)
                {
                    x++;
                }
                ForgeHooks.networkMods.put(x++, (NetworkMod)mod);
            }
        }
        //Add 131 & 132 to C->S list
        ((Set)ModLoader.getPrivateValue(Packet.class, null, 3)).add(131);
        ((Set)ModLoader.getPrivateValue(Packet.class, null, 3)).add(132);
    }
}
