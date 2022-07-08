/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Consumer;

/**
 * Fired to allow mods to register custom {@linkplain ShaderInstance shaders}.
 * This event is fired after the default Minecraft shaders have been registered.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class RegisterShadersEvent extends Event implements IModBusEvent
{
    private final ResourceManager resourceManager;
    private final List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList;

    @ApiStatus.Internal
    public RegisterShadersEvent(ResourceManager resourceManager, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList)
    {
        this.resourceManager = resourceManager;
        this.shaderList = shaderList;
    }

    /**
     * {@return the client-side resource manager}
     */
    public ResourceManager getResourceManager()
    {
        return resourceManager;
    }

    /**
     * Registers a shader, and a callback for when the shader is loaded.
     *
     * <p>When creating a {@link ShaderInstance}, pass in the {@linkplain #getResourceManager()
     * client-side resource manager} as the resource provider.</p>
     *
     * <p>Mods should not store the shader instance passed into this method. Instead, mods should store the shader
     * passed into the registered load callback.</p>
     *
     * @param shaderInstance a shader
     * @param onLoaded       a callback for when the shader is loaded
     */
    public void registerShader(ShaderInstance shaderInstance, Consumer<ShaderInstance> onLoaded)
    {
        shaderList.add(Pair.of(shaderInstance, onLoaded));
    }
}
