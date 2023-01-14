/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.world.entity.Entity;

/**
 * EntitySprintEvent is fired when {@link Entity#setSprinting(boolean)} is invoked.<br>
 * This event will not be fired when sprint state has not been actually changed, such as
 * invoking {@code entity.setSprint(true)} when entity is already sprinting.
 **/
public class EntitySprintEvent extends EntityEvent {
    public final boolean willSprint;

    public EntitySprintEvent(Entity entity, boolean willSprint) {
        super(entity);
        this.willSprint = willSprint;
    }
}
