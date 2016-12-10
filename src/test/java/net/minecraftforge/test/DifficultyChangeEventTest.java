package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.DifficultyChangeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraft.util.EnumHand;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

@Mod(modid = "difficultychangeeventtest", name = "DifficultyChangeEventTest", version = "0.0.0")
public class DifficultyChangeEventTest
{
    public static final boolean ENABLE = true;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLE)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDifficultyChange(DifficultyChangeEvent event)
    {
        if (event.getDifficulty() == EnumDifficulty.HARD)
            event.setDifficulty(EnumDifficulty.PEACEFUL);
        System.out.println("Difficulty changed from " + event.getOldDifficulty() + " to " + event.getDifficulty());
    }
}