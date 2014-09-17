package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Fired right after movement input is captured, but before it has been processed.
 * Useful for making movement input do different things temporarily.
 * @author Mithion
 *
 * Cancel this event if you set your own parameters for movement.  It will cause your input to be used
 * instead of MC's default input processing.  If you don't cancel it, changes are ignored.
 */
@Cancelable
public class MovementInputProcessingEvent extends Event 
{
    public float smoothCamFilterX;
    public float smoothCamFilterY;
    public float cameraPartialTicks;
    public float smoothCameraPitch;
    public float smoothCameraYaw;
    
    public MovementInputProcessingEvent(float camFilterX, float camFilterY, float partialTicks, float pitch, float yaw)
    {
        this.smoothCameraPitch = pitch;
        this.smoothCameraYaw = yaw;
        
        this.cameraPartialTicks = partialTicks;
        
        this.smoothCamFilterX = camFilterX;
        this.smoothCamFilterY = camFilterY;
    }
}
