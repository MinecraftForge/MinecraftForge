/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.minecraftforge.client.event;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Called when a block layer is going to be rendered in the world.
 * This event is fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * before a block layer is rendered.
 * Canceling this event prevents the block layer from being rendered.
 */
@Cancelable
public class RenderBlockRenderLayerEvent extends Event {

    private final RenderGlobal     context;
    private final BlockRenderLayer blockRenderLayer;
    private final int              pass;
    private final double           partialTicks;
    private final Entity           entity;

    private int chunksRendered;

    public RenderBlockRenderLayerEvent(final RenderGlobal renderGlobal, final BlockRenderLayer blockRenderLayer, final double partialTicks, final int pass, final Entity entity, final int chunksRendered) {
        this.context = renderGlobal;
        this.blockRenderLayer = blockRenderLayer;
        this.partialTicks = partialTicks;
        this.pass = pass;
        this.entity = entity;
        this.chunksRendered = chunksRendered;
    }

    public RenderGlobal getContext() {
        return this.context;
    }

    public BlockRenderLayer getBlockRenderLayer() {
        return this.blockRenderLayer;
    }

    public double getPartialTicks() {
        return this.partialTicks;
    }

    public int getPass() {
        return this.pass;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void incrementChunksRendered() {
        this.chunksRendered++;
    }

    public int getChunksRendered() {
        return this.chunksRendered;
    }

}