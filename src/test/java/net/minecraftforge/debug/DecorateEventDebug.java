package net.minecraftforge.debug;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = DecorateEventDebug.MODID, name = DecorateEventDebug.NAME, version = DecorateEventDebug.VERSION, acceptableRemoteVersions = "*")
public class DecorateEventDebug
{

    private static final boolean ENABLED = false;
    public static final String MODID = "decorateeventdebug";
    public static final String NAME = "DecorateEventDebug";
    public static final String VERSION = "1.0.0";

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.TERRAIN_GEN_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void decorateEvent(DecorateBiomeEvent.Decorate event)
    {
        event.setResult(Result.DENY);
    }
}
