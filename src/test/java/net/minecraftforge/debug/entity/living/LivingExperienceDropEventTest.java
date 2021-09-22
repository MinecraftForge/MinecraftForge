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

package net.minecraftforge.debug.entity.living;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("living_experience_drop_event_test")
public class LivingExperienceDropEventTest
{
    private static final boolean ENABLE = false;
    private static final Logger logger = LogManager.getLogger(LivingExperienceDropEventTest.class);

    public LivingExperienceDropEventTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingXpDropEvent);
    }

    public void onLivingXpDropEvent(LivingExperienceDropEvent event)
    {
        if (!ENABLE) return;
        event.setDroppedExperience(event.getDroppedExperience() * 2);
        logger.info("{} killed {} resulting in {} xp, after modification {}", event.getAttackingPlayer().getName().getString(), event.getEntity().getName().getString(), event.getOriginalExperience(), event.getDroppedExperience());
    }

}
