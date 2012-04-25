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
    @MLProp(info = "Set to false to reproduce a vinella bug that prevents mobs from spawning on inverted half-slabs and inverted stairs.")
    public static boolean SPAWNER_ALLOW_ON_INVERTED = true;
    
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
