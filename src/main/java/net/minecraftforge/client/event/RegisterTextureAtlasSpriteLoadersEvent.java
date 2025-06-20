/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.textures.ITextureAtlasSpriteLoader;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * Allows users to register custom {@link ITextureAtlasSpriteLoader texture atlas sprite loaders}.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public final class RegisterTextureAtlasSpriteLoadersEvent implements SelfDestructing, IModBusEvent {
    public static EventBus<RegisterTextureAtlasSpriteLoadersEvent> getBus(BusGroup modBusGroup) {
        return IModBusEvent.getBus(modBusGroup, RegisterTextureAtlasSpriteLoadersEvent.class);
    }

    private final BiMap<ResourceLocation, ITextureAtlasSpriteLoader> loaders;

    @ApiStatus.Internal
    public RegisterTextureAtlasSpriteLoadersEvent(BiMap<ResourceLocation, ITextureAtlasSpriteLoader> loaders) {
        this.loaders = loaders;
    }

    /**
     * Registers a custom {@link ITextureAtlasSpriteLoader sprite loader}.
     */
    public void register(String name, ITextureAtlasSpriteLoader loader) {
        @SuppressWarnings("removal")
        var key = ResourceLocation.fromNamespaceAndPath(ModLoadingContext.get().getActiveNamespace(), name);
        Preconditions.checkArgument(!loaders.containsKey(key), "Sprite loader already registered: " + key);
        Preconditions.checkArgument(!loaders.containsValue(loader), "Sprite loader already registered as " + loaders.inverse().get(loader));
        loaders.put(key, loader);
    }
}
