/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;

import net.minecraft.resources.Identifier;
import net.minecraftsingularity.client.textures.ITextureAtlasSpriteLoader;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Allows users to register custom {@link ITextureAtlasSpriteLoader texture atlas sprite loaders}.
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public final class RegisterTextureAtlasSpriteLoadersEvent extends MutableEvent implements SelfDestructing {
    public static final EventBus<RegisterTextureAtlasSpriteLoadersEvent> BUS = EventBus.create(RegisterTextureAtlasSpriteLoadersEvent.class);

    private final BiMap<Identifier, ITextureAtlasSpriteLoader> loaders;

    @ApiStatus.Internal
    public RegisterTextureAtlasSpriteLoadersEvent(BiMap<Identifier, ITextureAtlasSpriteLoader> loaders) {
        this.loaders = loaders;
    }

    /**
     * Registers a custom {@link ITextureAtlasSpriteLoader sprite loader}.
     * @param Identifier The namespace should match your mod's namespace, such as your mod ID
     */
    public void register(Identifier Identifier, ITextureAtlasSpriteLoader loader) {
        Preconditions.checkArgument(!loaders.containsKey(Identifier), "Sprite loader already registered: " + Identifier);
        Preconditions.checkArgument(!loaders.containsValue(loader), "Sprite loader already registered as " + loaders.inverse().get(loader));
        loaders.put(Identifier, loader);
    }
}
