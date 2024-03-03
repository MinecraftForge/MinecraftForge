/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * This event is fired whenever a player attacks an Entity in
 * EntityPlayer#attackTargetEntityWithCurrentItem(Entity).<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event has a result. {@link HasResult}<br>
 * DEFAULT: means the vanilla logic will determine if this a critical hit.<br>
 * DENY: it will not be a critical hit but the player still will attack<br>
 * ALLOW: this attack is forced to be critical
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@HasResult
public class CriticalHitEvent extends PlayerEvent
{
    private float damageModifier;
    private final float oldDamageModifier;
    private final Entity target;
    private final boolean vanillaCritical;
    
    public CriticalHitEvent(Player player, Entity target, float damageModifier, boolean vanillaCritical)
    {
        super(player);
        this.target = target;
        this.damageModifier = damageModifier;
        this.oldDamageModifier = damageModifier;
        this.vanillaCritical = vanillaCritical;
    }
    
    /**
    * The Entity that was damaged by the player.
    */
    public Entity getTarget()
    {
        return target;
    }
    
    /**
    * This set the damage multiplier for the hit.
    * If you set it to 0, then the particles are still generated but damage is not done.
    */
    public void setDamageModifier(float mod)
    {
        this.damageModifier = mod;
    }
    
    /**
    * The damage modifier for the hit.<br>
    * This is by default 1.5F for ciritcal hits and 1F for normal hits .
    */
    public float getDamageModifier()
    {
        return this.damageModifier;
    }

    /**
    * The orignal damage modifier for the hit wthout any changes.<br>
    * This is 1.5F for ciritcal hits and 1F for normal hits .
    */
    public float getOldDamageModifier()
    {
        return this.oldDamageModifier;
    }
    
    /**
    * Returns true if this hit was critical by vanilla
    */
    public boolean isVanillaCritical()
    {
        return vanillaCritical;
    }
}
