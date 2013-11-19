package net.minecraftforge.client.event;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.event.Event;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 6:07 PM, 9/5/13
 */
public class FOVUpdateEvent extends Event
{
    public final EntityPlayerSP entity;
    public final float fov;
    public float newfov;

    public FOVUpdateEvent(EntityPlayerSP entity, float fov)
    {
        this.entity = entity;
        this.fov = fov;
        this.newfov = fov;
    }
}
