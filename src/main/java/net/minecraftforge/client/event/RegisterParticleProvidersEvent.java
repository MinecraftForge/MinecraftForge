/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Fired for registering particle providers at the appropriate time.
 *
 * <p>{@link ParticleType}s must be registered during {@link RegisterEvent} as usual;
 * this event is only for the {@link ParticleProvider}s.</p>
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
@NullMarked
public final class RegisterParticleProvidersEvent extends MutableEvent {
    public static EventBus<RegisterParticleProvidersEvent> BUS = EventBus.create(RegisterParticleProvidersEvent.class);

    @Deprecated(forRemoval = true, since = "1.21.9")
    public static EventBus<RegisterParticleProvidersEvent> getBus(BusGroup modBusGroup) {
        return BUS;
    }

    private final ParticleResources particles;

    @ApiStatus.Internal
    public RegisterParticleProvidersEvent(ParticleResources particles) {
        this.particles = particles;
    }

    /**
     * <p>Registers a ParticleProvider for a non-json-based ParticleType.
     * These particles do not receive a list of texture sprites to use for rendering themselves.</p>
     *
     * <p>There must be <strong>no</strong> particle json with an ID matching the ParticleType,
     * or a redundant texture list error will occur when particle jsons load.</p>
     *
     * @param <T> ParticleOptions used by the ParticleType and ParticleProvider.
     * @param type ParticleType to register a ParticleProvider for.
     * @param provider ParticleProvider function responsible for providing that ParticleType's particles.
     */
    @SuppressWarnings("deprecation")
    public <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider) {
        particles.register(type, provider);
    }

    /**
     * <p>Registers a ParticleProvider for a json-based ParticleType.
     * Particle jsons define a list of texture sprites which the particle can use to render itself.</p>
     *
     * <p>A particle json with an ID matching the ParticleType <strong>must exist</strong> in the <code>particles</code> asset folder,
     * or a missing texture list error will occur when particle jsons load.</p>
     *
     * @param <T> ParticleOptions used by the ParticleType and SpriteParticleRegistration function.
     * @param type ParticleType to register a particle provider for.
     * @param registration SpriteParticleRegistration function responsible for providing that ParticleType's particles.
     */
    @SuppressWarnings("deprecation")
    public <T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type, ParticleResources.SpriteParticleRegistration<T> registration) {
        particles.register(type, registration);
    }
}
