package net.minecraftforge.client.event;

import net.minecraft.client.renderer.RenderGlobal;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class DrawMoonEvent extends Event
{
    public final RenderGlobal context;
    public float moonRed;
    public float moonGreen;
    public float moonBlue;
    
    public DrawMoonEvent(RenderGlobal context)
    {
        this.context = context;
        this.moonRed = moonGreen = moonBlue = 1;
    }
}
