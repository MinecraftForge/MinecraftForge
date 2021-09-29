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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.entity.player.PlayerDamageItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("player_damage_item_event_test")
@Mod.EventBusSubscriber()
public class PlayerDamageItemEventTest 
{
    private static final boolean ENABLE = false;
    private static final Logger LOGGER = LogManager.getLogger(PlayerDamageItemEventTest.class);

    @SubscribeEvent
    public static void onPlayerDamageItemEvent(PlayerDamageItemEvent event) 
    {
        if (!ENABLE) return;
        LOGGER.info("{} damaged item {}", event.getPlayer().getDisplayName().getString(), event.getStack().getDisplayName().getString());
        LOGGER.info("DamageValue:{}", event.getDamage());
        LOGGER.info("OriginalDamageValue:{}", event.getOrignalNewDamage());
        if (event.getPlayer().hasEffect(MobEffects.DIG_SPEED)) 
        {
            if (event.getStack().getItem() instanceof PickaxeItem) 
            {
                event.setCanceled(true);
                LOGGER.info("PlayerDamageItemEvent was canceled, because the damaged Item was an instance of PickaxeItem and the Player has the Haste Effect");
            } 
            else if (event.getStack().getItem() instanceof ShieldItem) 
            {
                event.setNewDamage(event.getOrignalNewDamage() / 2);
                LOGGER.info("The damage value {}, was bisect because the Item was an instance of ShieldItem damage value is now {}", event.getOrignalNewDamage(), event.getNewDamage());
            }
        }
        LOGGER.info("NewDamageValue:{}", event.getNewDamage());
    }
}
