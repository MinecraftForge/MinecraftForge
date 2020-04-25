/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.event.entity.player;

import net.minecraftforge.eventbus.api.Event.HasResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

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
    
    public CriticalHitEvent(PlayerEntity player, Entity target, float damageModifier, boolean vanillaCritical)
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
