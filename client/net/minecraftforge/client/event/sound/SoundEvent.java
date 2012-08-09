package net.minecraftforge.client.event.sound;

import net.minecraft.src.Entity;
import net.minecraft.src.SoundManager;
import net.minecraft.src.SoundPoolEntry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

public class SoundEvent extends Event
{
    public SoundEvent(){}
    
    public static SoundPoolEntry getResult(SoundResultEvent event)
    {
        MinecraftForge.EVENT_BUS.post(event);
        return event.result;
    }
}