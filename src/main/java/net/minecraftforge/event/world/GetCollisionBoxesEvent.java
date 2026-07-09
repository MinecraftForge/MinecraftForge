/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This event is fired during {@link World#collidesWithAnyBlock(AxisAlignedBB)}
 * and before returning the list in {@link World#getCollisionBoxes(Entity, AxisAlignedBB)}<br>
 * <br>
 * {@link #entity} contains the entity passed in the {@link World#getCollisionBoxes(Entity, AxisAlignedBB)}. <b>Can be null.</b> Calls from {@link World#collidesWithAnyBlock(AxisAlignedBB)} will be null.<br>
 * {@link #aabb} contains the AxisAlignedBB passed in the method.<br>
 * {@link #collisionBoxesList} contains the list of detected collision boxes intersecting with {@link #aabb}. The list can be modified.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult} <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class GetCollisionBoxesEvent extends WorldEvent
{
    private final Entity entity;
    private final AxisAlignedBB aabb;
    private final List<AxisAlignedBB> collisionBoxesList;

    public GetCollisionBoxesEvent(World world, @Nullable Entity entity, AxisAlignedBB aabb, List<AxisAlignedBB> collisionBoxesList)
    {
        super(world);
        this.entity = entity;
        this.aabb = aabb;
        this.collisionBoxesList = collisionBoxesList;
    }

    public Entity getEntity()
    {
        return entity;
    }

    public AxisAlignedBB getAabb()
    {
        return aabb;
    }

    public List<AxisAlignedBB> getCollisionBoxesList()
    {
        return collisionBoxesList;
    }
}
