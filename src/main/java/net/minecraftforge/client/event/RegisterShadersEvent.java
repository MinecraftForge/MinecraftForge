/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.List;
import java.util.function.Consumer;

public class RegisterShadersEvent extends Event implements IModBusEvent
{
    private final ResourceManager resourceManager;
    private final List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList;

    public RegisterShadersEvent(ResourceManager resourceManager, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList)
    {
        this.resourceManager = resourceManager;
        this.shaderList = shaderList;
    }

    public ResourceManager getResourceManager()
    {
        return resourceManager;
    }

    public void registerShader(ShaderInstance shaderInstance, Consumer<ShaderInstance> onLoaded)
    {
        shaderList.add(Pair.of(shaderInstance, onLoaded));
    }
}
