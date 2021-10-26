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

package net.minecraftforge.event.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class MinecartEvent extends Event
{

    private final AbstractMinecart minecart;

    public MinecartEvent(AbstractMinecart minecart)
    {
        this.minecart = minecart;
    }

    public AbstractMinecart getMinecart()
    {
        return minecart;
    }

    /**
     * This event is called when a player sitting inside of a minecart steers it (by "walking" while inside a minecart).
     * The fields x and y represent the source speed, which is the speed of the player divided by ten.
     */
    public static class Steered extends MinecartEvent
    {

        private final Player player;
        private double x, z;

        public Steered(AbstractMinecart minecart, Player player, double x, double z)
        {
            super(minecart);
            this.player = player;
            this.x = x;
            this.z = z;
        }

        public Player getPlayer()
        {
            return player;
        }

        public double getX()
        {
            return x;
        }

        public void setX(double x)
        {
            this.x = x;
        }

        public double getZ()
        {
            return z;
        }

        public void setZ(double z)
        {
            this.z = z;
        }

    }

    /**
     * This event is called when an entity (including another minecart) pushes a minecart.
     * The pushing strength can be modified here.
     */
    public static class Pushed extends MinecartEvent
    {

        private final Entity pusher;
        private double pushX;
        private double pushZ;

        public Pushed(AbstractMinecart minecart, Entity pusher, double pushX, double pushZ)
        {
            super(minecart);
            this.pusher = pusher;
            this.pushX = pushX;
            this.pushZ = pushZ;
        }

        public double getPushX()
        {
            return pushX;
        }

        public void setPushX(double pushX)
        {
            this.pushX = pushX;
        }

        public double getPushZ()
        {
            return pushZ;
        }

        public void setPushZ(double pushZ)
        {
            this.pushZ = pushZ;
        }

        public Entity getPusher()
        {
            return pusher;
        }

    }

    /**
     * This event is called at the beginning of {@link AbstractMinecart#moveAlongTrack(BlockPos, BlockState)}.
     * If cancelled, the default minecart movement code will not run.
     */
    @Cancelable
    public static class PreMove extends MinecartEvent
    {

        private final BlockPos railPos;
        private final BlockState railState;

        public PreMove(AbstractMinecart minecart, BlockPos railPos, BlockState railState)
        {
            super(minecart);
            this.railPos = railPos;
            this.railState = railState;
        }

        public BlockPos getRailPos()
        {
            return railPos;
        }

        public BlockState getRailState()
        {
            return railState;
        }

    }

    /**
     * This event is called at the end of {@link AbstractMinecart#moveAlongTrack(BlockPos, BlockState)} to apply changes after movement has commenced.
     */
    @net.minecraftforge.eventbus.api.Cancelable
    public static class PostMove extends MinecartEvent
    {

        private final BlockPos railPos;
        private final BlockState railState;

        public PostMove(AbstractMinecart minecart, BlockPos railPos, BlockState railState)
        {
            super(minecart);
            this.railPos = railPos;
            this.railState = railState;
        }

        public BlockPos getRailPos()
        {
            return railPos;
        }

        public BlockState getRailState()
        {
            return railState;
        }

    }
}
