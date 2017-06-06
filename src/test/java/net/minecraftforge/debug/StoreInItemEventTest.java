package net.minecraftforge.debug;

import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.StoreInItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "storeinitemtestmod", name = "StroreInItemEvent Test Mod", version = "0.0.0", acceptableRemoteVersions = "*")
public class StoreInItemEventTest
{
    public static final boolean ENABLE = true;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt)
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onStoreInItem(StoreInItemEvent event)
    {
        if (event.getResult() == Event.Result.DEFAULT && event.getInput().getItem() == Items.BEEF)
        {
            event.setResult(Event.Result.DENY);
        }
    }
}