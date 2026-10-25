/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraftforge.common.util.HasResult;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NullMarked;

/// @deprecated This event is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
///             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
///             We may keep it or add it back in a future version if there is enough demand.
@Deprecated(forRemoval = true, since = "26.4")
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
