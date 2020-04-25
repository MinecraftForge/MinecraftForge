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

package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nonnull;

/**
 * {@link LivingEquipmentChangeEvent} is fired when the Equipment of a Entity changes. <br>
 * This event is fired whenever changes in Equipment are detected in {@link EntityLivingBase#onUpdate()}. <br>
 * This also includes entities joining the World, as well as being cloned. <br>
 * This event is fired on server-side only. <br>
 * <br>
 * {@link #slot} contains the affected {@link EntityEquipmentSlot}. <br>
 * {@link #from} contains the {@link ItemStack} that was equipped previously. <br>
 * {@link #to} contains the {@link ItemStack} that is equipped now. <br>
 * <br>
 * This event is not {@link Cancelable}. <br>
 * <br>
 * This event does not have a result. {@link HasResult} <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class LivingEquipmentChangeEvent extends LivingEvent
{
    private final EquipmentSlotType slot;
    private final ItemStack from;
    private final ItemStack to;

    public LivingEquipmentChangeEvent(LivingEntity entity, EquipmentSlotType slot, @Nonnull ItemStack from, @Nonnull ItemStack to)
    {
        super(entity);
        this.slot = slot;
        this.from = from;
        this.to = to;
    }

    public EquipmentSlotType getSlot() { return this.slot; }
    @Nonnull
    public ItemStack getFrom() { return this.from; }
    @Nonnull
    public ItemStack getTo() { return this.to; }
}
