package net.minecraftforge.client.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 6:07 PM, 9/5/13
 */
public class FOVUpdateEvent extends Event
{
    public final EntityPlayer entity;
    public final float fov;
    public float newfov;

    public FOVUpdateEvent(EntityPlayer entity, float fov)
    {
        this.entity = entity;
        this.fov = fov;
        this.newfov = fov;
    }
}
