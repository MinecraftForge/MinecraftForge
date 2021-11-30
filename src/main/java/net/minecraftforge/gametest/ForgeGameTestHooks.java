package net.minecraftforge.gametest;

import net.minecraft.SharedConstants;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.fml.ModLoader;

import java.util.Set;

public class ForgeGameTestHooks
{
    public static boolean isGametestEnabled()
    {
        return SharedConstants.IS_RUNNING_IN_IDE || Boolean.getBoolean("forge.enablegametest");
    }

    public static void registerGametests(Set<String> enabledNamespaces)
    {
        if (isGametestEnabled())
        {
            ModLoader.get().postEvent(new RegisterGameTestsEvent(enabledNamespaces));
        }
    }
}
