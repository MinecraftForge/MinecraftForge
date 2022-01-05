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

package net.minecraftforge.debug.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * The ShieldBlockTest is the test mod for the ShieldBlockEvent.
 * If successful, this handler will trigger when an arrow is blocked by a player.
 * The event will give the arrow to the player, and make the player receive half of the damage (instead of zero damage).
 * Note this just gives them a normal arrow, retrieving the true arrow requires some reflection.
 */
@Mod(ShieldBlockTest.MOD_ID)
@Mod.EventBusSubscriber
public class ShieldBlockTest
{
    static final String MOD_ID = "shield_block_event";

    @SubscribeEvent
    public static void shieldBlock(ShieldBlockEvent event)
    {
        if (event.getDamageSource().getDirectEntity() instanceof AbstractArrow arrow && event.getEntityLiving() instanceof Player player)
        {
            player.getInventory().add(new ItemStack(Items.ARROW));
            event.setBlockedDamage(event.getOriginalBlockedDamage() / 2);
            arrow.discard();
        }
    }
}
