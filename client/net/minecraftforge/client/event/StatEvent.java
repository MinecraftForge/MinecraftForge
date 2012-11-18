package net.minecraftforge.client.event;

import net.minecraft.src.StatBase;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class StatEvent extends Event
{
    public StatBase stat;
    public int incrementAmount;
    public StatEvent(StatBase stat, int incrementAmount)
    {
        this.stat = stat;
        this.incrementAmount = incrementAmount;
    }
}
