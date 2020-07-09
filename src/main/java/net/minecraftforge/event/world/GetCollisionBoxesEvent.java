/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
