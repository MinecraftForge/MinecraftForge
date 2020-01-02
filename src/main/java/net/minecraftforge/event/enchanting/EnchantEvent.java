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

package net.minecraftforge.event.enchanting;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * EnchantEvent is fired whenever an item will be enchanted in enchantment table.<br>
 * <br>
 * The event is fired during the {@link net.minecraft.inventory.container.EnchantmentContainer#enchantItem(PlayerEntity, int)} method invocation.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the enchantment is not being added.<br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 */

@Cancelable
public class EnchantEvent extends Event {

    private final ItemStack stack;
    private Enchantment enchantment;
    private int level;

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
     * Set the enchantment
     *
     * @param enchantment the enchantment which replaces another one
     */
    public void setEnchantment(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    /**
     * Set the enchantment
     *
     * @param enchantment the enchantment which replaces another one
     * @param level       the level of the enchantment
     */
    public void setEnchantment(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        setLevel(level);
    }

    /**
     * Get the level of the enchantment
     *
     * @return the level of the enchantment
     */
    public int getLevel() {
        return level;
    }

    /**
     * Set the enchantment
     *
     * @param level the level of the enchantment
     */
    public void setLevel(int level) {
        this.level = level;
        if (level > getEnchantment().getMaxLevel()) this.level = getEnchantment().getMaxLevel();
    }

}
