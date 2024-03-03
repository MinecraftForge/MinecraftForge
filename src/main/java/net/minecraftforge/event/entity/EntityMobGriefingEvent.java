/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * EntityMobGriefingEvent is fired when mob griefing is about to occur and allows an event listener to specify whether it should or not.<br>
 * This event is fired when ever the {@code mobGriefing} game rule is checked.<br>
 * <br>
 * This event has a {@link HasResult result}:
 * <ul>
 * <li>{@link Result#ALLOW} means this instance of mob griefing is allowed.</li>
 * <li>{@link Result#DEFAULT} means the {@code mobGriefing} game rule is used to determine the behaviour.</li>
 * <li>{@link Result#DENY} means this instance of mob griefing is not allowed.</li>
 * </ul>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@HasResult
public class EntityMobGriefingEvent extends EntityEvent
{
    public EntityMobGriefingEvent(Entity entity)
    {
        super(entity);
    }
}
