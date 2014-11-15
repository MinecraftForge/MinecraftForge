package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Event;

public class CloudColorEvent extends Event
{
    public float red;
    public float green;
    public float blue;
    
    public CloudColorEvent(float red, float green, float blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}
