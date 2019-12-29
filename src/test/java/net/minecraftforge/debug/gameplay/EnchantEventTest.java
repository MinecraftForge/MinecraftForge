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

package net.minecraftforge.debug.gameplay;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.enchanting.EnchantEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.debug.gameplay.EnchantEventTest.MODID;

@Mod(MODID)
public class EnchantEventTest {
    static final String MODID = "enchant_event_test";
    private static Logger LOGGER = LogManager.getLogger(MODID);

    public EnchantEventTest() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEnchant(EnchantEvent event) {
        ItemStack stack = event.getItem();
        String itemName = stack.getDisplayName().getFormattedText();
        Enchantment enchantment = event.getEnchantment();
        String enchantmentName = enchantment.getDisplayName(event.getLevel()).getFormattedText();
        LOGGER.info("{} was enchanted with {} {}", itemName, enchantmentName, event.getLevel());
    }
}
