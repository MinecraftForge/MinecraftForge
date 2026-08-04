/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.renderer.WorldRenderer;

/**
 * This event is fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * before both hands are rendered.
 * Canceling this event prevents either hand from being rendered,
 * and prevents {@link RenderSpecificHandEvent} from firing.
 * TODO This may get merged in 11 with RenderSpecificHandEvent to make a generic hand rendering
 */
@net.minecraftforge.eventbus.api.Cancelable
public class RenderHandEvent extends net.minecraftforge.eventbus.api.Event
{
    private final WorldRenderer context;
    private final float partialTicks;
    public RenderHandEvent(WorldRenderer context, float partialTicks)
    {
        this.context = context;
        this.partialTicks = partialTicks;
    }

    public WorldRenderer getContext()
    {
        return context;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }
}
