package net.minecraftforge.test;

import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEditSignEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "forgesigntest")
public class SignEditTest
{
    public static final boolean ENABLE = false;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onSignEdit(PlayerEditSignEvent event)
    {
        if(ENABLE)
        {
            for (IChatComponent component : event.getText())
            {
                System.out.println(component.getUnformattedText());
            }
            //event.setCanceled(true);
        }
    }
}
