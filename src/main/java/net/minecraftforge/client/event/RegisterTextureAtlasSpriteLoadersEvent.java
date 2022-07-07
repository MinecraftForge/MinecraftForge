/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.textures.ITextureAtlasSpriteLoader;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * Allows users to register custom {@link ITextureAtlasSpriteLoader texture atlas sprite loaders}.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class RegisterTextureAtlasSpriteLoadersEvent extends Event implements IModBusEvent
{
    private final Map<ResourceLocation, ITextureAtlasSpriteLoader> loaders;

    @ApiStatus.Internal
    public RegisterTextureAtlasSpriteLoadersEvent(Map<ResourceLocation, ITextureAtlasSpriteLoader> loaders)
    {
        this.loaders = loaders;
    }

    /**
     * Registers a custom {@link ITextureAtlasSpriteLoader sprite loader}.
     */
    public void register(String name, ITextureAtlasSpriteLoader loader)
    {
        var key = new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name);
        Preconditions.checkArgument(!loaders.containsKey(key), "Sprite loader already registered: " + key);
        loaders.put(key, loader);
    }
}
