/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraftsingularity.common.util.HasResult;
import net.minecraftsingularity.common.util.Result;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;

/**
 * This event is fired whenever a player attacks an Entity in
 * EntityPlayer#attackTargetEntityWithCurrentItem(Entity).<br>
 * <br>
 * This event {@linkplain HasResult has a result}:
 * <ul>
 *     <li>{@link Result#DEFAULT}: means the vanilla logic will determine if this a critical hit.</li>
 *     <li>{@link Result#DENY}: it will not be a critical hit but the player still will attack</li>
 *     <li>{@link Result#ALLOW}: this attack is forced to be critical</li>
 * </ul>
 **/
public final class CriticalHitEvent extends MutableEvent implements PlayerEvent, HasResult {
    public static final EventBus<CriticalHitEvent> BUS = EventBus.create(CriticalHitEvent.class);

    private final Player player;
    private float damageModifier;
    private final float oldDamageModifier;
    private final Entity target;
    private final boolean vanillaCritical;
    private Result result = Result.DEFAULT;
    
    public CriticalHitEvent(Player player, Entity target, float damageModifier, boolean vanillaCritical) {
        this.player = player;
        this.target = target;
        this.damageModifier = damageModifier;
        this.oldDamageModifier = damageModifier;
        this.vanillaCritical = vanillaCritical;
    }

    @Override
    public Player getEntity() {
        return player;
    }
    
    /**
    * The Entity that was damaged by the player.
    */
    public Entity getTarget() {
        return target;
    }
    
    /**
    * This set the damage multiplier for the hit.
    * If you set it to 0, then the particles are still generated but damage is not done.
    */
    public void setDamageModifier(float mod) {
        this.damageModifier = mod;
    }
    
    /**
    * The damage modifier for the hit.<br>
    * This is by default 1.5F for ciritcal hits and 1F for normal hits .
    */
    public float getDamageModifier() {
        return this.damageModifier;
    }

    /**
    * The orignal damage modifier for the hit wthout any changes.<br>
    * This is 1.5F for ciritcal hits and 1F for normal hits .
    */
    public float getOldDamageModifier() {
        return this.oldDamageModifier;
    }
    
    /**
    * Returns true if this hit was critical by vanilla
    */
    public boolean isVanillaCritical() {
        return vanillaCritical;
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
