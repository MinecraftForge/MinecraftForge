package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * Created by Simeon on 12/8/2015.
 */
public class MouseSensitivityUpdateEvent extends Event
{
    public final float sensitivity;
    public float newSensitivity;

    public MouseSensitivityUpdateEvent(float sensitivity)
    {
        this.sensitivity = sensitivity;
        this.newSensitivity = sensitivity;
    }
}
