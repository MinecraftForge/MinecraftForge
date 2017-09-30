package net.minecraftforge.debug;

import net.minecraftforge.event.CrashEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//This test mod will print any crash log to console a second time when they are created.
//This shows how to intercept crash logs to provide modders a way to generate reports and maybe 
//some features for autoreporting them automatically if they believe it was caused by their mod
@Mod(modid="testcrashevent", version="1.0", name="Test Crash Event", acceptableRemoteVersions="*")
@Mod.EventBusSubscriber
public class CrashEventTest 
{
    @SubscribeEvent
    public static void onCrash(CrashEvent evt) 
    {
        //This could easily be used for posting the log to pastebin and returning the link to the user
        System.out.println(evt.getReport().getCompleteReport());
    }
}
