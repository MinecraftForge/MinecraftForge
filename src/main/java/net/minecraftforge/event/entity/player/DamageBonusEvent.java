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

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * This event is fired whenever a player attacks an Entity in
 * Player#attack(Entity).<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This allows adding to the base damage like DamageEnchantments do, but also considering the attacked Entity object including capabilities, not just the Entity's MobType
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class DamageBonusEvent extends PlayerEvent
{
    private final ItemStack weapon;
    private final Entity target;
    private float bonus;

    public DamageBonusEvent(Player player, ItemStack weapon, Entity target, float enchantmentBonus)
    {
        super(player);
        this.weapon = weapon;
        this.target = target;
        this.bonus = enchantmentBonus;
    }

    /**
     * @return The attacked entity
     */
    public Entity getTarget()
    {
        return target;
    }

    /**
     * @return The item used for the attack
     */
    public ItemStack getWeapon()
    {
        return weapon;
    }

    /**
     * @param additionalDamage Added to the existing bonus damage
     */
    public void addBonus(float additionalDamage)
    {
        this.bonus += additionalDamage;
    }

    /**
     * @return The total bonus damage (enchantments + damage already added to this event)
     */
    public float getBonus()
    {
        return this.bonus;
    }
}
