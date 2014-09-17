package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * Called on each render pass right after the camera's perspective is set.
 * @author Mithion
 *
 */
public class CameraSetupEvent extends Event
{
    //use this if you need to rotate the world
    public static final byte POST_PERSPECTIVE = 0;
    //use this if you need to shift the position of the camera
    public static final byte POST_ORIENT = 0;
    
    public final float f;
    public final int i;
    
    public final byte operation;
    
    public CameraSetupEvent(float p_78479_1_, int p_78479_2_, byte operation)
    {
        this.f = p_78479_1_;
        this.i = p_78479_2_;
        this.operation = operation;
    }

}
