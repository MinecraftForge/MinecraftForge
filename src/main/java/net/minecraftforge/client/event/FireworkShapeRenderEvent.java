/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;

/**
 *
 * Fired when a non-default (aka non-minecraft) firework is attempting to be rendered
 *
 */
public class FireworkShapeRenderEvent extends Event
{
    private final Particle particle;
    private final Vec3 particlePos;
    private final FireworkRocketItem.Shape shape;
    private final boolean trail;
    private final boolean flickers;
    private final int[] colors;
    private final int[] fadeColors;
    private final ParticleRenderer particleShapeRenderer;

    public FireworkShapeRenderEvent(Particle particle, Vec3 particlePos, FireworkRocketItem.Shape shape, boolean trail, boolean flickers, int[] colors, int[] fadeColors, ParticleRenderer particleShapeRenderer)
    {
        this.particle = particle;
        this.particlePos = particlePos;
        this.shape = shape;
        this.trail = trail;
        this.flickers = flickers;
        this.colors = colors;
        this.fadeColors = fadeColors;
        this.particleShapeRenderer = particleShapeRenderer;
    }

    public Particle getParticle() {
        return this.particle;
    }

    public FireworkRocketItem.Shape getShape()
    {
        return this.shape;
    }

    public boolean isTrail()
    {
        return this.trail;
    }

    public boolean isFlickers()
    {
        return this.flickers;
    }

    public int[] getColors()
    {
        return this.colors;
    }

    public int[] getFadeColors()
    {
        return this.fadeColors;
    }

    public ParticleRenderer getParticleShapeRenderer() {
        return this.particleShapeRenderer;
    }

    public Vec3 getParticlePos() {
        return this.particlePos;
    }

    /**
     *
     * An interface for rendering the particle to the world at the given position
     *
     */
    public interface ParticleRenderer
    {

        void render(double x, double y, double z, double xOffset, double yOffset, double zOffset, int[] color, int[] fadeColor, boolean trail, boolean flickers);

    }
}
