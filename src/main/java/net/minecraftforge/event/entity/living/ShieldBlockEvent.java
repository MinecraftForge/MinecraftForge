/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.event.entity.living;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * The ShieldBlockEvent is fired when an entity successfully blocks with a shield.<br>
 * Cancelling this event will have the same impact as if the shield was not eligible to block.<br>
 * The damage blocked cannot be set lower than zero or greater than the original value.<br>
 * Note: The shield item stack "should" be available from {@link LivingEntity#getUseItem()}
 * at least for players.
 */
@Cancelable
public class ShieldBlockEvent extends LivingEvent
{
    private final DamageSource source;
    private final float originalBlocked;
    private float dmgBlocked;
    private boolean shieldTakesDamage = true;

    public ShieldBlockEvent(LivingEntity blocker, DamageSource source, float blocked)
    {
        super(blocker);
        this.source = source;
        this.originalBlocked = blocked;
        this.dmgBlocked = blocked;
    }

    /**
     * @return The damage source.
     */
    public DamageSource getDamageSource()
    {
        return this.source;
    }

    /**
     * @return The original amount of damage blocked, which is the same as the original
     * incoming damage value.
     */
    public float getOriginalBlockedDamage()
    {
        return this.originalBlocked;
    }

    /**
     * @return The current amount of damage blocked, as a result of this event.
     */
    public float getBlockedDamage()
    {
        return this.dmgBlocked;
    }

    /**
     * Controls if {@link LivingEntity#hurtCurrentlyUsedShield} is called.
     * @return If the shield item will take durability damage or not.
     */
    public boolean shieldTakesDamage()
    {
        return this.shieldTakesDamage;
    }

    /**
     * Set how much damage is blocked by this action.<br>
     * Note that initially the blocked amount is the entire attack.<br>
     */
    public void setBlockedDamage(float blocked)
    {
        this.dmgBlocked = Mth.clamp(blocked, 0, this.originalBlocked);
    }

    /**
     * Set if the shield will take durability damage or not.
     */
    public void setShieldTakesDamage(boolean damage)
    {
        this.shieldTakesDamage = damage;
    }

}
