/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.common;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;

/**
 * This class defines a replacement for the default minecart collision code.
 * Only one handler can be registered at a time. It it registered with EntityMinecart.registerCollisionHandler().
 * If you use this, make it a configuration option.
 * @author CovertJaguar
 */
public interface IMinecartCollisionHandler
{

    /**
     * This basically replaces the function of the same name in EntityMinecart.
     * Code in IMinecartHooks.applyEntityCollisionHook is still run.
     * @param cart The cart that called the collision.
     * @param other The object it collided with.
     */
    void onEntityCollision(EntityMinecart cart, Entity other);

    /**
     * This function replaced the function of the same name in EntityMinecart.
     * It is used to define whether minecarts collide with specific entities,
     * for example items.
     * @param cart The cart for which the collision box was requested.
     * @param other The entity requesting the collision box.
     * @return The collision box or null.
     */
    AxisAlignedBB getCollisionBox(EntityMinecart cart, Entity other);

    /**
     * This function is used to define the box used for detecting minecart collisions.
     * It is generally bigger that the normal collision box.
     * @param cart The cart for which the collision box was requested.
     * @return The collision box, cannot be null.
     */
    AxisAlignedBB getMinecartCollisionBox(EntityMinecart cart);

    /**
     * This function replaces the function of the same name in EntityMinecart.
     * It defines whether minecarts are solid to the player.
     * @param cart The cart for which the bounding box was requested.
     * @return The bounding box or null.
     */
    AxisAlignedBB getBoundingBox(EntityMinecart cart);
}

