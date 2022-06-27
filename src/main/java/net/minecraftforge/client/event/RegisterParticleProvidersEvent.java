/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired for registering particle providers at the appropriate time.
 *
 * <p>{@link ParticleType}s must be registered during {@link RegisterEvent} as usual;
 * this event is only for the {@link ParticleProvider}s.</p>
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class RegisterParticleProvidersEvent extends Event implements IModBusEvent
{
    private final ParticleEngine particleEngine;

    @ApiStatus.Internal
    public RegisterParticleProvidersEvent(ParticleEngine particleEngine)
    {
        this.particleEngine = particleEngine;
    }

    public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider)
    {
        particleEngine.register(type, provider);
    }

    public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> registration)
    {
        particleEngine.register(type, registration);
    }
}
