/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

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
public record EntityMountEvent(
        Entity entityMounting,
        Entity entityBeingMounted,
        Level level,
        boolean isMounting
) implements Cancellable, EntityEvent {
    @Override
    public Entity entity() {
        return entityMounting;
    }

    public boolean isDismounting()
    {
        return !isMounting;
    }
}
