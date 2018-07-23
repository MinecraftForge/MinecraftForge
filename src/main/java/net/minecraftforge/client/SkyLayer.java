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

package net.minecraftforge.client;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;

public class SkyLayer {
    public final ResourceLocation id;
    private Group group = null;
    private IRenderHandler renderer = null;

    SkyLayer(ResourceLocation idIn)
    {
        this.id = idIn;
    }

    /**
     * Makes this layer as a layer group of sub-layers.
     * @return the group reference as a result
     * */
    public Group makeGroup()
    {
        this.renderer = null;
        this.group = new Group();
        return this.group;
    }

    /**
     * @return the layer group of this layer if it's a group, or <code>null</code> otherwise
     * */
    public @Nullable Group getGroup()
    {
        return this.group;
    }

    /**
     * Replace the renderer assigned to this layer with the specified renderer.
     * @param rendererIn the renderer to set
     * */
    public void replace(IRenderHandler rendererIn)
    {
        this.group = null;
        this.renderer = rendererIn;
    }

    /**
     * @return renderer assigned to this layer, or <code>null</code> if it doesn't present
     * */
    public @Nullable IRenderHandler getRenderer()
    {
        return this.renderer;
    }

    public class Group {
        public final SkyLayer layer;

        Group()
        {
            this.layer = SkyLayer.this;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof SkyLayer)
        {
            return this.id.equals(((SkyLayer)o).id);
        }
        else return false;
    }

    @Override
    public int hashCode()
    {
        return this.id.hashCode();
    }
}
