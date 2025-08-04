/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.Map;

import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;

public final class CreateSpecialBlockRendererEvent extends MutableEvent {
    public static final EventBus<CreateSpecialBlockRendererEvent> BUS = EventBus.create(CreateSpecialBlockRendererEvent.class);

    private final Map<Block, SpecialModelRenderer.Unbaked> map;

    public CreateSpecialBlockRendererEvent(Map<Block, SpecialModelRenderer.Unbaked> map) {
        this.map = map;
    }

    public void register(Block block, SpecialModelRenderer.Unbaked renderer) {
        this.map.put(block, renderer);
    }
}
