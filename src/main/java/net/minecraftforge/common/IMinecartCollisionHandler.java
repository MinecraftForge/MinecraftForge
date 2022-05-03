/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;

/**
 * This class defines a replacement for the default minecart collision code.
 * Only one handler can be registered at a time. It it registered with AbstractMinecartEntity.registerCollisionHandler().
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
    void onEntityCollision(AbstractMinecartEntity cart, Entity other);

    /**
     * This function replaced the function of the same name in EntityMinecart.
     * It is used to define whether minecarts collide with specific entities,
     * for example items.
     * @param cart The cart for which the collision box was requested.
     * @param other The entity requesting the collision box.
     * @return The collision box or null.
     */
    AxisAlignedBB getCollisionBox(AbstractMinecartEntity cart, Entity other);

    /**
     * This function is used to define the box used for detecting minecart collisions.
     * It is generally bigger that the normal collision box.
     * @param cart The cart for which the collision box was requested.
     * @return The collision box, cannot be null.
     */
    AxisAlignedBB getMinecartCollisionBox(AbstractMinecartEntity cart);

    /**
     * This function replaces the function of the same name in EntityMinecart.
     * It defines whether minecarts are solid to the player.
     * @param cart The cart for which the bounding box was requested.
     * @return The bounding box or null.
     */
    AxisAlignedBB getBoundingBox(AbstractMinecartEntity cart);
}

