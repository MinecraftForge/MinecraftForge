/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.gameplay.advancement;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.PositionTrigger;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(name = "advancementcriteriontest", modid = "advancementcriteriontest", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class AdvancementCriterionTest {
    private static final PositionTrigger TRIGGER = new PositionTrigger(new ResourceLocation("advancementcriteriontest", "position"));

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt)
    {
        CriteriaTriggers.register(TRIGGER);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent evt)
    {
        if (evt.side == Side.SERVER && evt.phase == TickEvent.Phase.END)
        {
            TRIGGER.trigger((EntityPlayerMP) evt.player);
        }
    }
}
