package net.minecraftforge.client.event;

import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class DrawHoveringTextEvent extends Event
{
    public final List textLines;
    public final int posX;
    public final int posY;
    
    public DrawHoveringTextEvent(List textLines, int posX, int posY)
    {
        this.textLines = textLines;
        this.posX = posX;
        this.posY = posY;
    }
}
