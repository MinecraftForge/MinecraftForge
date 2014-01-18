package net.minecraftforge.client.event.sound;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraftforge.common.MinecraftForge;

public class SoundEvent extends Event
{
    public SoundEvent(){}
    
    public static SoundPoolEntry getResult(SoundResultEvent event)
    {
        MinecraftForge.EVENT_BUS.post(event);
        return event.result;
    }
}