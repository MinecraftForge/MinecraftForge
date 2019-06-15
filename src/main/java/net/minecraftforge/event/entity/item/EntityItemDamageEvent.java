/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.event.entity.item;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Event that is fired when an EntityItem is damaged from an outside source.
 * This event is called after the EntityItem is checked for invulnerability to the DamageSource.<br>
 * <br>
 * {@link #source} contains the {@link DamageSource} that caused this entity to be damaged.<br>
 * {@link #amount} contains the final amount of damage that will be dealt to the Entity.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the EntityItem is not damaged.
 */
@Cancelable
public class EntityItemDamageEvent extends ItemEvent
{
    private final DamageSource source;
    private float amount;

    /**
     * Creates a new event for an EntityItem that is taking damage.
     *
     * @param entityItem The EntityItem being damaged.
     * @param source The {@link DamageSource} source of the damage.
     * @param amount The amount of damage being dealt.
     */
    public EntityItemDamageEvent(EntityItem entityItem, DamageSource source, float amount)
    {
        super(entityItem);
        this.source = source;
        this.amount = amount;
    }

    public DamageSource getSource()
    {
        return source;
    }

    public float getAmount()
    {
        return amount;
    }

    public void setAmount(float amount)
    {
        this.amount = amount;
    }
}
