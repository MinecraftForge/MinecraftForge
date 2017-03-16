package net.minecraftforge.test;

import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.DifficultyChangeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "difficultychangeeventtest", name = "DifficultyChangeEventTest", version = "0.0.0")
public class DifficultyChangeEventTest
{
    private static final boolean ENABLE = false;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLE) MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDifficultyChange(DifficultyChangeEvent event)
    {
        System.out.println("Difficulty changed from " + event.getOldDifficulty() + " to " + event.getDifficulty());
    }
}