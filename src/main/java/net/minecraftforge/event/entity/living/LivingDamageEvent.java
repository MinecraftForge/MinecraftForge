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

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.LivingEntity;

/**
 * LivingDamageEvent is fired just before damage is applied to entity.<br>
 * At this point armor, potion and absorption modifiers have already been applied to damage - this is FINAL value.<br>
 * Also note that appropriate resources (like armor durability and absorption extra hearths) have already been consumed.<br>
 * This event is fired whenever an Entity is damaged in
 * {@link EntityLivingBase#damageEntity(DamageSource, float)} and
 * {@link EntityPlayer#damageEntity(DamageSource, float)}.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingDamage(EntityLivingBase, DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused this Entity to be hurt. <br>
 * {@link #amount} contains the final amount of damage that will be dealt to entity. <br>
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * If this event is canceled, the Entity is not hurt. Used resources WILL NOT be restored.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * @see LivingHurtEvent
 **/
@Cancelable
public class LivingDamageEvent extends LivingEvent
{
    private final DamageSource source;
    private float amount;
    public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount)
    {
        super(entity);
        this.source = source;
        this.amount = amount;
    }

    public DamageSource getSource() { return source; }

    public float getAmount() { return amount; }

    public void setAmount(float amount) { this.amount = amount; }
}
