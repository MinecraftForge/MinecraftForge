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

package net.minecraftforge.event;

import net.minecraft.particles.IParticleData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraftforge.eventbus.api.Event;

/**
 * ParticleWorldHandleEvent is fired before an {@link SSpawnParticlePacket} is sent.<br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If the event is canceled, the execution of the command does not occur.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@Cancelable
public class ParticleWorldHandleEvent extends Event {

    private IParticleData particle;
    private boolean longDistance;
    private int particleCount;
    private double particleSpeed;
    private double xCoord;
    private double yCoord;
    private double zCoord;
    private double xOffset;
    private double yOffset;
    private double zOffset;

    public ParticleWorldHandleEvent(IParticleData particle, boolean longDistance, int particleCount, double particleSpeed, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset) {
        this.particle = particle;
        this.longDistance = longDistance;
        this.particleCount = particleCount;
        this.particleSpeed = particleSpeed;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    public IParticleData getParticle() {
        return particle;
    }

    public void setParticle(IParticleData particle) {
        this.particle = particle;
    }

    public boolean isLongDistance() {
        return longDistance;
    }

    public void setLongDistance(boolean longDistance) {
        this.longDistance = longDistance;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public void setParticleCount(int particleCount) {
        this.particleCount = particleCount;
    }

    public double getParticleSpeed() {
        return particleSpeed;
    }

    public void setParticleSpeed(double particleSpeed) {
        this.particleSpeed = particleSpeed;
    }

    public double getXCoord() {
        return xCoord;
    }

    public void setXCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    public double getYCoord() {
        return yCoord;
    }

    public void setYCoord(double yCoord) {
        this.yCoord = yCoord;
    }

    public double getZCoord() {
        return zCoord;
    }

    public void setZCoord(double zCoord) {
        this.zCoord = zCoord;
    }

    public double getXOffset() {
        return xOffset;
    }

    public void setXOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    public double getYOffset() {
        return yOffset;
    }

    public void setYOffset(double yOffset) {
        this.yOffset = yOffset;
    }

    public double getZOffset() {
        return zOffset;
    }

    public void setZOffset(double zOffset) {
        this.zOffset = zOffset;
    }
}
