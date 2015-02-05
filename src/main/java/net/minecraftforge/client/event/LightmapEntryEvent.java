package net.minecraftforge.client.event;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class LightmapEntryEvent extends Event
{
    public final EntityRenderer renderer;
    public final int position;
    public final float partialTicks;
    
    public final int originalRed;
    public final int originalGreen;
    public final int originalBlue;
    
    public int newRed;
    public int newGreen;
    public int newBlue;
    
    public LightmapEntryEvent(EntityRenderer renderer,int position,int originalRed,int originalGreen,int originalBlue,float partialTicks)
    {
        this.renderer = renderer;
        this.position = position;
        this.partialTicks = partialTicks;
        
        this.originalRed = this.newRed = originalRed;
        this.originalGreen = this.newGreen = originalGreen;
        this.originalBlue = this.newBlue = originalBlue;
    }
}
