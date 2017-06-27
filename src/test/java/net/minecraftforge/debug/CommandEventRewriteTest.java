package net.minecraftforge.debug;

import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = CommandEventRewriteTest.MODID, name = "Command Event Rewrite Test", version = "0.0.0")
@EventBusSubscriber
public class CommandEventRewriteTest
{
    public static final String MODID = "commandeventrewritetest";
    public static final boolean ENABLED = false;

    /**
     * Makes the teleport command usable by all the players.
     */
    @SubscribeEvent
    public static void onCommand(CommandEvent event)
    {
        if (ENABLED)
        {
            if (event.getCommand().getName().equals("tp"))
            {
                event.setResult(Result.ALLOW);
            }
        }
    }
}
