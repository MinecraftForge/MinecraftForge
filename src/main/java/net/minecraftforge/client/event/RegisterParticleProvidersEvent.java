/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.TextureSheetParticle;
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

    /**
     * Registers a ParticleProvider for a non-json-based ParticleType.
     * These particles do not receive a list of texture sprites to use for rendering themselves.
     * @param <T> ParticleOptions used by the ParticleType and ParticleProvider.
     * @param type ParticleType to register a ParticleProvider for.
     * There must be no particle json with an ID matching this ParticleType,
     * or a redundant texture list error will occur when particle jsons load.
     * @param provider ParticleProvider function responsible for providing that ParticleType's particles.
     */
    @SuppressWarnings("deprecation")
    public <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider) {
        particleEngine.register(type, provider);
    }
    
    /**
     * Registers a ParticleProvider for a non-json-based ParticleType.
     * These particles do not receive a list of texture sprites to use for rendering themselves.
     * @param <T> ParticleOptions used by the ParticleType and ParticleProvider.
     * @param type ParticleType to register a ParticleProvider for.
     * There must be no particle json with an ID matching this ParticleType,
     * or a redundant texture list error will occur when particle jsons load.
     * @param provider ParticleProvider function responsible for providing that ParticleType's particles.
     * @deprecated Renamed to {@link #registerSpecial}
     * @see #registerSpecial
     */
    @Deprecated(forRemoval=true, since="1.19.4")
    public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider)
    {
        registerSpecial(type, provider);
    }

    /**
     * Registers a ParticleProvider for a json-based ParticleType with a single texture;
     * the resulting {@link TextureSheetParticle}s will use that texture when created.
     * @param <T> ParticleOptions used by the ParticleType and Sprite function.
     * @param type ParticleType to register a ParticleProvider for.
     * There must be a particle json with an ID matching this ParticleType in the <code>particles</code> asset folder,
     * or a missing texture list error will occur when particle jsons load.
     * @param sprite Sprite function responsible for providing that ParticleType's particles.
     */
    @SuppressWarnings("deprecation")
    public <T extends ParticleOptions> void registerSprite(ParticleType<T> type, ParticleProvider.Sprite<T> sprite)
    {
        particleEngine.register(type, sprite);
    }
    
    /**
     * Registers a ParticleProvider for a json-based ParticleType with a single texture;
     * the resulting {@link TextureSheetParticle}s will use that texture when created.
     * @param <T> ParticleOptions used by the ParticleType and Sprite function.
     * @param type ParticleType to register a ParticleProvider for.
     * There must be a particle json with an ID matching this ParticleType in the <code>particles</code> asset folder,
     * or a missing texture list error will occur when particle jsons load.
     * @param sprite Sprite function responsible for providing that ParticleType's particles.
     * @deprecated Renamed to {@link #registerSprite}
     * @see #registerSprite
     */
    @Deprecated(forRemoval=true, since="1.19.4")
    public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider.Sprite<T> sprite)
    {
        registerSprite(type, sprite);
    }

    /**
     * Registers a ParticleProvider for a json-based ParticleType.
     * Particle jsons define a list of texture sprites which the particle can use to render itself.
     * @param <T> ParticleOptions used by the ParticleType and SpriteParticleRegistration function.
     * @param type ParticleType to register a particle prov for.
     * There must be a particle json with an ID matching this ParticleType in the <code>particles</code> asset folder,
     * or a missing texture list error will occur when particle jsons load.
     * @param registration SpriteParticleRegistration function responsible for providing that ParticleType's particles.
     */
    @SuppressWarnings("deprecation")
    public <T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> registration)
    {
        particleEngine.register(type, registration);
    }
    
    /**
     * Registers a ParticleProvider for a json-based ParticleType.
     * Particle jsons define a list of texture sprites which the particle can use to render itself.
     * @param <T> ParticleOptions used by the ParticleType and SpriteParticleRegistration function.
     * @param type ParticleType to register a particle prov for.
     * There must be a particle json with an ID matching this ParticleType in the <code>particles</code> asset folder,
     * or a missing texture list error will occur when particle jsons load.
     * @param registration SpriteParticleRegistration function responsible for providing that ParticleType's particles.
     * @deprecated Renamed to {@link #registerSpriteSet}
     * @see #registerSpriteSet
     */
    @Deprecated(forRemoval=true, since="1.19.4")
    public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> registration)
    {
        registerSpriteSet(type, registration);
    }
}
