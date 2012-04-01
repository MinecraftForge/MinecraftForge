package net.minecraft.src;

import fml.Loader;
import fml.ModContainer;
import net.minecraft.src.forge.ForgeHooks;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.NetworkMod;

/**
 * This class is just here to make the Forge version show up nicely in the ModLoader logs/Crash Screen
 */
public class mod_MinecraftForge extends NetworkMod
{
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
      int id=0;
      for (ModContainer mod : Loader.getModList()) {
        if (mod.getMod() instanceof NetworkMod) {
          Loader.log.fine(String.format("Assigning NetworkMod %s the forge id %d",mod.getName(),id));
          ForgeHooks.networkMods.put(id++, (NetworkMod)mod.getMod());
        }
      }
    }

	@Override
	public boolean clientSideRequired() 
	{
		return false;
	}

	@Override
	public boolean serverSideRequired() 
	{
		return false;
	}
}
