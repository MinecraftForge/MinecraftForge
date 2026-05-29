/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraftsingularity.common.util.HasResult;
import net.minecraftsingularity.common.util.Result;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NullMarked;

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
 */
@NullMarked
public final class EntityMobGriefingEvent extends MutableEvent implements EntityEvent, HasResult {
    public static final EventBus<EntityMobGriefingEvent> BUS = EventBus.create(EntityMobGriefingEvent.class);

    private final Entity entity;
    private Result result = Result.DEFAULT;

    public EntityMobGriefingEvent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public void setResult(Result result) {
        this.result = result;
    }
}
