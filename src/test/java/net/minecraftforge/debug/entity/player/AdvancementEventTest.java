/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent.ProgressType;
import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AdvancementEventTest.MOD_ID)
public class AdvancementEventTest
{
    public static final String MOD_ID = "advancement_event";
    public static final boolean ENABLED = false;
    private static final Logger LOGGER = LogManager.getLogger();

    public AdvancementEventTest()
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.addListener(this::onAdvancementEarnEvent);
            MinecraftForge.EVENT_BUS.addListener(this::onAdvancementProgressEvent);
        }
    }

    public void onAdvancementEarnEvent(AdvancementEarnEvent event)
    {
        Advancement advancement = event.getAdvancement();
        Player player = event.getPlayer();
        LOGGER.info("Player {} earned advancement {} and was awarded {}", player, advancement.getId(), advancement.getRewards().toString());
    }
    public void onAdvancementProgressEvent(AdvancementProgressEvent event)
    {
        Advancement advancement = event.getAdvancement();
        Player player = event.getPlayer();
        AdvancementProgress advancementProgress = event.getAdvancementProgress();
        String criterionName = event.getCriterionName();
        AdvancementEvent.AdvancementProgressEvent.ProgressType progressType = event.getProgressType();
        String action;
        if (progressType == ProgressType.GRANT)
        {
            action = "granted";
        }
        else
        {
            action = "revoked";
        }
        LOGGER.info("Player {} progressed advancement {}. It was {} progress on {} criterionName. It has completed {}% of the achievement", player, advancement.getId(), action, criterionName, advancementProgress.getPercent()*100);
    }
}
