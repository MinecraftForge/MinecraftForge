package net.minecraftforge.test;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "createfluidsourcetest", name = "CreateFluidSourceTest", version = "1.0", acceptableRemoteVersions = "*")
public class CreateFluidSourceTest
{
    public static final boolean ENABLE = false;

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event)
    {
        if (ENABLE)
            MinecraftForge.EVENT_BUS.register(CreateFluidSourceTest.class);
    }

    @SubscribeEvent
    public static void onCreateFluidSource(CreateFluidSourceEvent event)
    {
        // make it work exactly the opposite of how it works by default
        if (event.getState().getBlock() == Blocks.FLOWING_WATER)
            event.setResult(Result.DENY);
        else
            event.setResult(Result.ALLOW);
    }
}
