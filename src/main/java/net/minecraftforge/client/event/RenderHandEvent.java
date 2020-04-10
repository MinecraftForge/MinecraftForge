/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.RenderGlobal;

/**
 * This event is fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * before both hands are rendered.
 * Canceling this event prevents either hand from being rendered,
 * and prevents {@link RenderSpecificHandEvent} from firing.
 * TODO This may get merged in 11 with RenderSpecificHandEvent to make a generic hand rendering
 */
@Cancelable
public class RenderHandEvent extends Event
{
    private final RenderGlobal context;
    private final float partialTicks;
    private final int renderPass;
    public RenderHandEvent(RenderGlobal context, float partialTicks, int renderPass)
    {
        this.context = context;
        this.partialTicks = partialTicks;
        this.renderPass = renderPass;
    }

    public RenderGlobal getContext()
    {
        return context;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    public int getRenderPass()
    {
        return renderPass;
    }
}
