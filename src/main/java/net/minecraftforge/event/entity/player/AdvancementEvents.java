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

package net.minecraftforge.event.entity.player;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

public class AdvancementEvents
{
    /**
     * This event is fired when a player earns an advancement/recipe.
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     */
    public static class AdvancementEarnEvent extends PlayerEvent
    {
        private final Advancement advancement;

        public AdvancementEarnEvent(Player player, Advancement earned)
        {
            super(player);
            this.advancement = earned;
        }

        public Advancement getAdvancement()
        {
            return advancement;
        }
    }

    /**
     * This event is fired when a player progresses on an advancement criterion.
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     */
    public static class AdvancementProgressEvent extends PlayerEvent
    {
        public enum ProgressType
        {
            GRANT,REVOKE
        }

        private final Advancement advancement;
        public final AdvancementProgress advancementProgress;
        public final String criterion;
        public final ProgressType progressType;

        /**
         *
         * @param player The Event's {@link net.minecraft.world.entity.player.Player}
         * @param progressed The {@link Advancement} that was progressed
         * @param advancementProgress The {@link net.minecraft.advancements.AdvancementProgress} of the {@link net.minecraft.advancements.Advancement}
         * @param criterion The {@link String} representing the {@link net.minecraft.advancements.CriterionProgress} that was progressed.
         * @param progressType Defines what {@link net.minecraftforge.event.entity.player.AdvancementEvents.AdvancementProgressEvent.ProgressType} was the {@link net.minecraft.advancements.CriterionProgress}
         */
        public AdvancementProgressEvent(Player player, Advancement progressed, AdvancementProgress advancementProgress, String criterion, ProgressType progressType)
        {
            super(player);
            this.advancement = progressed;
            this.advancementProgress = advancementProgress;
            this.criterion = criterion;
            this.progressType = progressType;
        }

        public Advancement getAdvancement()
        {
            return advancement;
        }
    }
}
