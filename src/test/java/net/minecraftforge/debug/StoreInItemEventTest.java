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
        if (event.getInput().getItem()==Items.STICK)
        {
            boolean deny = true;
            for (int i = 0;i<event.getContents().size();i++) {
                if (event.getContents().get(i).getItem()==Items.BLAZE_ROD)
                {
                    deny = false;
                }
            }
            if (deny)
            {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
