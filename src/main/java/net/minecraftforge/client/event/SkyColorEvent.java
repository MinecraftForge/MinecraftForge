package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Event;

public class SkyColorEvent extends Event
{
    int red;
    int green;
    int blue;
    
	public SkyColorEvent(int r, int g, int b)
    {
    	this.red = r;
    	this.green = g;
    	this.blue = b;
    }
}
