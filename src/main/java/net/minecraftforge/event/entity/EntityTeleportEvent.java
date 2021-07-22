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

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * EntityTeleportEvent is fired when an event involving any teleportation of an Entity occurs.<br>
 * If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #getTarget()} contains the target destination.<br>
 * {@link #getPrev()} contains the entity's current position.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@Cancelable
public class EntityTeleportEvent extends EntityEvent
{
    protected double targetX;
    protected double targetY;
    protected double targetZ;

    public EntityTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
        super(entity);
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    public double getTargetX() { return targetX; }
    public void setTargetX(double targetX) { this.targetX = targetX; }
    public double getTargetY() { return targetY; }
    public void setTargetY(double targetY) { this.targetY = targetY; }
    public double getTargetZ() { return targetZ; }
    public void setTargetZ(double targetZ) { this.targetZ = targetZ; }
    public Vec3 getTarget() { return new Vec3(this.targetX, this.targetY, this.targetZ); }
    public double getPrevX() { return getEntity().getX(); }
    public double getPrevY() { return getEntity().getY(); }
    public double getPrevZ() { return getEntity().getZ(); }
    public Vec3 getPrev() { return getEntity().position(); }

    /**
     * EntityTeleportEvent.TeleportCommand is fired before a living entity is teleported
     * from use of {@link net.minecraft.command.impl.TeleportCommand}.
     * <br>
     * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * If the event is not canceled, the entity will be teleported.
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     * <br>
     * This event is only fired on the {@link net.minecraftforge.fml.LogicalSide#SERVER} side.<br>
     * <br>
     * If this event is canceled, the entity will not be teleported.
     */
    @Cancelable
    public static class TeleportCommand extends EntityTeleportEvent
    {
        public TeleportCommand(Entity entity, double targetX, double targetY, double targetZ)
        {
            super(entity, targetX, targetY, targetZ);
        }
    }

    /**
     * EntityTeleportEvent.SpreadPlayersCommand is fired before a living entity is teleported
     * from use of {@link net.minecraft.command.impl.SpreadPlayersCommand}.
     * <br>
     * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * If the event is not canceled, the entity will be teleported.
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     * <br>
     * This event is only fired on the {@link net.minecraftforge.fml.LogicalSide#SERVER} side.<br>
     * <br>
     * If this event is canceled, the entity will not be teleported.
     */
    @Cancelable
    public static class SpreadPlayersCommand extends EntityTeleportEvent
    {
        public SpreadPlayersCommand(Entity entity, double targetX, double targetY, double targetZ)
        {
            super(entity, targetX, targetY, targetZ);
        }
    }

    /**
     * EntityTeleportEvent.EnderEntity is fired before an Enderman or Shulker randomly teleports.
     * <br>
     * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * If the event is not canceled, the entity will be teleported.
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     * <br>
     * This event is only fired on the {@link net.minecraftforge.fml.LogicalSide#SERVER} side.<br>
     * <br>
     * If this event is canceled, the entity will not be teleported.
     */
    @Cancelable
    public static class EnderEntity extends EntityTeleportEvent
    {
        private final LivingEntity entityLiving;

        public EnderEntity(LivingEntity entity, double targetX, double targetY, double targetZ)
        {
            super(entity, targetX, targetY, targetZ);
            this.entityLiving = entity;
        }

        public LivingEntity getEntityLiving()
        {
            return entityLiving;
        }
    }

    /**
     * EntityTeleportEvent.EnderPearl is fired before an Entity is teleported from an EnderPearlEntity.
     * <br>
     * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * If the event is not canceled, the entity will be teleported.
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     * <br>
     * This event is only fired on the {@link net.minecraftforge.fml.LogicalSide#SERVER} side.<br>
     * <br>
     * If this event is canceled, the entity will not be teleported.
     */
    @Cancelable
    public static class EnderPearl extends EntityTeleportEvent
    {
        private final ServerPlayer player;
        private final ThrownEnderpearl pearlEntity;
        private float attackDamage;

        public EnderPearl(ServerPlayer entity, double targetX, double targetY, double targetZ, ThrownEnderpearl pearlEntity, float attackDamage)
        {
            super(entity, targetX, targetY, targetZ);
            this.pearlEntity = pearlEntity;
            this.player = entity;
            this.attackDamage = attackDamage;
        }

        public ThrownEnderpearl getPearlEntity()
        {
            return pearlEntity;
        }

        public ServerPlayer getPlayer()
        {
            return player;
        }

        public float getAttackDamage()
        {
            return attackDamage;
        }

        public void setAttackDamage(float attackDamage)
        {
            this.attackDamage = attackDamage;
        }
    }

    /**
     * EntityTeleportEvent.ChorusFruit is fired before a LivingEntity is teleported due to consuming Chorus Fruit.
     * <br>
     * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * If the event is not canceled, the entity will be teleported.
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     * <br>
     * This event is only fired on the {@link net.minecraftforge.fml.LogicalSide#SERVER} side.<br>
     * <br>
     * If this event is canceled, the entity will not be teleported.
     */
    @Cancelable
    public static class ChorusFruit extends EntityTeleportEvent
    {
        private final LivingEntity entityLiving;

        public ChorusFruit(LivingEntity entity, double targetX, double targetY, double targetZ)
        {
            super(entity, targetX, targetY, targetZ);
            this.entityLiving = entity;
        }

        public LivingEntity getEntityLiving()
        {
            return entityLiving;
        }
    }
}
