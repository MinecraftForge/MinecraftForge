/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.util.SortedRegistry;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * Fired during setup on {@link net.minecraftforge.api.distmarker.Dist#CLIENT}
 * to register custom render types for static block rendering.
 *
 * @see Solid
 * @see Translucent
 * @see Tripwire
 */
public abstract class LayerRenderTypeRegisterEvent extends Event implements IModBusEvent
{
    private final SortedRegistry.Builder<RenderType> builder;

    protected LayerRenderTypeRegisterEvent(SortedRegistry.Builder<RenderType> builder)
    {
        this.builder = builder;
    }

    public SortedRegistry.Builder<RenderType> getBuilder()
    {
        return builder;
    }

    /**
     * Variant of {@link LayerRenderTypeRegisterEvent} for solid render types.
     * Vanilla includes {@link RenderType#solid()}, {@link RenderType#cutoutMipped()}
     * and {@link RenderType#cutout()} in that order.
     */
    public static class Solid extends LayerRenderTypeRegisterEvent
    {
        public Solid(SortedRegistry.Builder<RenderType> builder)
        {
            super(builder);
        }
    }

    /**
     * Variant of {@link LayerRenderTypeRegisterEvent} for translucent render types.
     * Vanilla includes {@link RenderType#translucent()} only.
     */
    public static class Translucent extends LayerRenderTypeRegisterEvent
    {
        public Translucent(SortedRegistry.Builder<RenderType> builder)
        {
            super(builder);
        }
    }

    /**
     * Variant of {@link LayerRenderTypeRegisterEvent} for tripwire like render types.
     * Vanilla includes {@link RenderType#tripwire()} only.
     */
    public static class Tripwire extends LayerRenderTypeRegisterEvent
    {
        public Tripwire(SortedRegistry.Builder<RenderType> builder)
        {
            super(builder);
        }
    }
}
