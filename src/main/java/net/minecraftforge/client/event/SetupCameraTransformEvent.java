package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * Created by Simeon on 12/8/2015.
 */
public class SetupCameraTransformEvent extends Event
{
    public final float partialTicks;
    public final int pass;

    public SetupCameraTransformEvent(float partialTicks,int pass)
    {
        this.partialTicks = partialTicks;
        this.pass = pass;
    }
}
