package net.minecraftforge.test;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "creaturetypespawntest", name = "creaturetypespawntest", version = "0.0.0")
public class CreatureTypeSpawnTest
{
    private static final boolean ENABLE = false;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLE) MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCreatureTypeSpawn(WorldEvent.CreatureTypeSpawnEvent event)
    {
        if (event.getState().getBlock() == Blocks.WATER) event.setResult(Event.Result.ALLOW);
        else event.setResult(Event.Result.DENY);
    }
}
