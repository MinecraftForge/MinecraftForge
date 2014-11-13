package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Event;

public class SkyColorEvent extends Event
{
    public int red;
    public int green;
    public int blue;
    
	public SkyColorEvent(int r, int g, int b)
    {
    	this.red = r;
    	this.green = g;
    	this.blue = b;
    }
}
