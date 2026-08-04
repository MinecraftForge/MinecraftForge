/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.renderer.WorldRenderer;

public class RenderWorldLastEvent extends net.minecraftforge.eventbus.api.Event
{
    private final WorldRenderer context;
    private final float partialTicks;
    public RenderWorldLastEvent(WorldRenderer context, float partialTicks)
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
