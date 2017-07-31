/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * When an advancement is granted to a player.
 */
public class AdvancementGrantEvent extends PlayerEvent
{
    private final Advancement advancement;

    public AdvancementGrantEvent(EntityPlayer player, Advancement advancement)
    {
        super(player);
        this.advancement = advancement;
    }

    public Advancement getAdvancement()
    {
        return advancement;
    }

    @Cancelable
    public static class Pre extends AdvancementGrantEvent
    {
        public Pre(EntityPlayer player, Advancement advancement)
        {
            super(player, advancement);
        }
    }

    public static class Post extends AdvancementGrantEvent
    {
        public Post(EntityPlayer player, Advancement advancement)
        {
            super(player, advancement);
        }
    }
}
