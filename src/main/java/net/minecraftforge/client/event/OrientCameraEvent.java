package net.minecraftforge.client.event;

import net.minecraft.entity.EntityLivingBase;
import cpw.mods.fml.common.eventhandler.Event;

public class OrientCameraEvent extends Event
{
    public final float partialTicks;
    public final EntityLivingBase entity;
    
    public OrientCameraEvent(EntityLivingBase entity, float partialTicks)
    {
        this.entity = entity;
        this.partialTicks = partialTicks;
    }
}
