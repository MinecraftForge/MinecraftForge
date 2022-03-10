/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event gets fired whenever a entity mounts/dismounts another entity.<br>
 * <b>entityBeingMounted can be null</b>, be sure to check for that.
 * <br>
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * If this event is canceled, the entity does not mount/dismount the other entity.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 *<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 *
 */

@Cancelable
public class EntityMountEvent extends EntityEvent
{

    private final Entity entityMounting;
    private final Entity entityBeingMounted;
    private final Level worldObj;

    private final boolean isMounting;

    public EntityMountEvent(Entity entityMounting, Entity entityBeingMounted, Level entityWorld, boolean isMounting)
    {
        super(entityMounting);
        this.entityMounting = entityMounting;
        this.entityBeingMounted = entityBeingMounted;
        this.worldObj = entityWorld;
        this.isMounting = isMounting;
    }

    public boolean isMounting()
    {
        return isMounting;
    }

    public boolean isDismounting()
    {
        return !isMounting;
    }

    public Entity getEntityMounting()
    {
        return entityMounting;
    }

    public Entity getEntityBeingMounted()
    {
        return entityBeingMounted;
    }

    public Level getWorldObj()
    {
        return worldObj;
    }
}
