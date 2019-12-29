/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.event.enchanting;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/**
 * EnchantEvent is fired whenever you add an enchantment to an item (no anvil)<br>
 * <br>
 * The event is fired during the {@link ItemStack#addEnchantment(Enchantment, int)} method invocation.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 */
public class EnchantEvent extends Event {

    private final ItemStack stack;
    private final Enchantment enchantment;
    private final int level;

    public EnchantEvent(ItemStack stack, Enchantment enchantment, int level) {
        this.stack = stack;
        this.enchantment = enchantment;
        this.level = level;
    }

    /**
     * Get the enchanted item
     *
     * @return the enchanted item
     */
    public ItemStack getItem() {
        return stack;
    }

    /**
     * Get the enchantment
     *
     * @return the enchantment
     */
    public Enchantment getEnchantment() {
        return enchantment;
    }

    /**
     * Get the level of the enchantment
     *
     * @return the level of the enchantment
     */
    public int getLevel() {
        return level;
    }
}
