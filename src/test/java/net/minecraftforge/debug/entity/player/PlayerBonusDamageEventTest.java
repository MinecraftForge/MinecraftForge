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

package net.minecraftforge.debug.entity.player;

import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.DamageBonusEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * When enabled, any weapon enchanted with smite should instantly kill a saddled pig, unless the player attack is on cooldown.
 * Verify:
 * 1. Get iron sword with smite
 * 2. Spawn 3 pigs, saddle two of them
 * 3. Attack the pig without a saddle: It should take serveral hits
 * 4. Attack a saddled pig: It should die with a single hit
 * 5. Swing your sword in the air, then immediately hit the last pig (so your attack cooldown is active): The pig should not die immediately
 */
@Mod("player_bonus_damage_event_test")
@Mod.EventBusSubscriber()
public class PlayerBonusDamageEventTest
{
    private static final boolean ENABLE = false;

    @SubscribeEvent
    public static void onDamageBonusEvent(DamageBonusEvent event)
    {
        if (!ENABLE) return;
        if(EnchantmentHelper.getEnchantments(event.getWeapon()).containsKey(Enchantments.SMITE))
        {
            if(event.getTarget() instanceof Pig pig)
            {
                if(pig.isSaddled())
                {
                    event.addBonus(10);
                }
            }
        }
    }
}