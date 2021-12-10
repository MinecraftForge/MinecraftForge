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

package net.minecraftforge.client.event;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * The base event for a collection of event that allow modders to control
 * the animations of the player model. This event is fired on the
 * {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * <p>
 * Use one of the sub classed events to perform modifications
 * <ul>
 * <li>{@link Blank} to animate on a unmodified model. Can be {@link Cancelable}</li>
 * <li>{@link Pre} to animate before player specific animations. Can be {@link Cancelable}</li>
 * <li>{@link Post} to animate after player specific animations</li>
 * </ul>
 */
public abstract class AnimatePlayerEvent extends PlayerEvent
{
    private final PlayerModel<?> model;

    public AnimatePlayerEvent(Player player, PlayerModel<?> model)
    {
        super(player);
        this.model = model;
    }

    /**
     * Gets the model of the player
     */
    public PlayerModel<?> getModel()
    {
        return model;
    }

    /**
     * An event fired before any animations are applied the player. The player will have it's
     * default pose and is essentially a blank canvas. If the event is not cancelled, any animations
     * applied will be potentially overwritten by Minecraft and/or other mods. This event is cancellable.
     */
    @Cancelable
    public static class Blank extends AnimatePlayerEvent
    {
        public Blank(Player player, PlayerModel<?> model)
        {
            super(player, model);
        }
    }

    /**
     * An event fired after initial animations (walking, arm poses, etc) are applied to the
     * player but before player specific animations are introduced (like copying the angles of
     * sleeves to the arm). This event is not guaranteed to be called since {@link Blank}
     * could be cancelled. This event is cancellable.
     */
    @Cancelable
    public static class Pre extends AnimatePlayerEvent
    {
        public Pre(Player player, PlayerModel<?> model)
        {
            super(player, model);
        }
    }

    /**
     * An event fired after applying all animations to the player. This event is not guaranteed
     * to be called since {@link Blank} or {@link Pre} could be cancelled. This event is not
     * cancellable.
     */
    public static class Post extends AnimatePlayerEvent
    {
        public Post(Player player, PlayerModel<?> model)
        {
            super(player, model);
        }
    }
}
