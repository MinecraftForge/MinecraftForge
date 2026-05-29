/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraftsingularity.common.util.HasResult;
import net.minecraft.world.entity.Mob;
import net.minecraftsingularity.common.util.Result;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class LivingPackSizeEvent extends MutableEvent implements LivingEvent, HasResult {
    public static final EventBus<LivingPackSizeEvent> BUS = EventBus.create(LivingPackSizeEvent.class);

    private final Mob entity;
    private int maxPackSize;
    private Result result = Result.DEFAULT;
    
    public LivingPackSizeEvent(Mob entity) {
        this.entity = entity;
    }

    @Override
    public Mob getEntity() {
        return entity;
    }

    /**
     * This event is fired when the spawning system determines the
     * maximum amount of the selected entity that can spawn at the same
     * time.
     *
     * If you set the result to 'ALLOW', it means that you want to return
     * the value of maxPackSize as the maximum pack size for current entity.
     */
    public int getMaxPackSize() {
        return maxPackSize;
    }

    public void setMaxPackSize(int maxPackSize) {
        this.maxPackSize = maxPackSize;
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
