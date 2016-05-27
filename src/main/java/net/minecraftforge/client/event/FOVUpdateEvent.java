package net.minecraftforge.client.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 6:07 PM, 9/5/13
 */
public class FOVUpdateEvent extends Event
{
    private final EntityPlayer entity;
    private final float fov;
    private float newfov;

    public FOVUpdateEvent(EntityPlayer entity, float fov)
    {
        this.entity = entity;
        this.fov = fov;
        this.setNewfov(fov);
    }

    public EntityPlayer getEntity()
    {
        return entity;
    }

    public float getFov()
    {
        return fov;
    }

    public float getNewfov()
    {
        return newfov;
    }

    public void setNewfov(float newfov)
    {
        this.newfov = newfov;
    }
}
