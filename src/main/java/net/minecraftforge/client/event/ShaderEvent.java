/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * Defines events fired for shaders.
 */
public abstract class ShaderEvent extends Event
{

    private final ShaderInstance instance;

    @ApiStatus.Internal
    public ShaderEvent(ShaderInstance instance)
    {
        this.instance = instance;
    }

    public ShaderInstance getShader()
    {
        return instance;
    }

    /**
     * Event fired when a shader programs shaders has been attached but not linked.
     * Allows for the initialization of attachments.
     */
    public static class OnAttachmentInitialization extends ShaderEvent implements IModBusEvent
    {

        @ApiStatus.Internal
        public OnAttachmentInitialization(ShaderInstance instance)
        {
            super(instance);
        }
    }
}
