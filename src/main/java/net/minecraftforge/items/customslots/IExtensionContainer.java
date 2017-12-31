/*
 * Minecraft Forge
 * Copyright (c) 2017.
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

package net.minecraftforge.items.customslots;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Gives access to information about a provider of extension slots.
 */
public interface IExtensionContainer
{
    /**
     * Gets the entity this container is extending
     *
     * @return The living entity
     */
    EntityLivingBase getOwner();

    /**
     * Gets the list of slots contained within this extension
     *
     * @return The list of extension slots
     */
    ImmutableList<IExtensionSlot> getSlots();

    /**
     * Gets a slot by its unique id.
     *
     * @param slotId A resource location identifying the slot within this container.
     * @return The slot matching the requested id.
     */
    @Nullable
    IExtensionSlot getSlot(String slotId);
}
