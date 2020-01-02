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

package net.minecraftforge.debug.gameplay;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.enchanting.EnchantEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EnchantEventTest.MODID)
public class EnchantEventTest {
    static final String MODID = "enchant_event_test";
    private static Logger LOGGER = LogManager.getLogger(MODID);

    public EnchantEventTest() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEnchant(EnchantEvent event) {
        Enchantment originEnchantment = event.getEnchantment();
        // disable unbreaking
        if (event.getEnchantment() == Enchantments.UNBREAKING) {
            LOGGER.info("Unbreaking was skipped.");
            event.setCanceled(true);
        }

        // convert Bane of Arthropods and Smite to Sharpness with same level
        else if (event.getEnchantment() instanceof DamageEnchantment) {
            LOGGER.info("{} was replaced by Sharpness {}", originEnchantment.getDisplayName(event.getLevel()).getFormattedText(), event.getLevel());
            event.setEnchantment(Enchantments.SHARPNESS);
        }

        else {
            ItemStack stack = event.getItem();
            String itemName = stack.getDisplayName().getFormattedText();
            Enchantment enchantment = event.getEnchantment();
            String enchantmentName = enchantment.getDisplayName(event.getLevel()).getFormattedText();
            LOGGER.info("{} was enchanted with {} {}", itemName, enchantmentName, event.getLevel());
        }

        if (event.getEnchantment() == Enchantments.SHARPNESS)
            event.getPlayer().addPotionEffect(new EffectInstance(Effects.STRENGTH, 600 * 20, 2, false, false));
    }
}
