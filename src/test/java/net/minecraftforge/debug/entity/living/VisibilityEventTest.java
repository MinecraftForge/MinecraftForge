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

package net.minecraftforge.debug.entity.living;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingVisibilityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * This test mod makes skeletons not see entities which wear a leather chestplate.
 * This test mod quadruples the visibility of entities which wear a golden chestplate.
 * This test mod demonstrates that the deprecated {link: PlayerEvent.Visibility} is still called.
 */
@Mod(value = VisibilityEventTest.MODID)
public class VisibilityEventTest
{

	public static final String MODID = "visibility_event_test";
	private static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public VisibilityEventTest() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onLivingVisibility(LivingVisibilityEvent event)
	{
		double prevVisibility = event.getVisibility();
		if(event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.LEATHER_CHESTPLATE) {
			if(event.getLookingEntity() != null && event.getLookingEntity() instanceof SkeletonEntity) {
				event.setVisibility(0F);
				LOGGER.info("Setting visibility for entity " + event.getEntityLiving().getName().getFormattedText()
						+ " from " + prevVisibility + " to " + event.getVisibility() + ".");
				return;
			}
		}
		if(event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.GOLDEN_CHESTPLATE) {
			event.setVisibility(event.getVisibility() * 4.0);
			LOGGER.info("Setting visibility for entity " + event.getEntityLiving().getName().getFormattedText()
					+ " from " + prevVisibility + " to " + event.getVisibility() + ".");
			return;
		}
	}
	
	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public void onPlayerVisibility(PlayerEvent.Visibility event) {
		LOGGER.info("Deprecated PlayerEvent.Visibility was called.");
	}
}
