/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.renderer.texture.AtlasTexture;

import java.util.Set;


public class TextureStitchEvent extends Event
{
    private final AtlasTexture map;

    public TextureStitchEvent(AtlasTexture map)
    {
        this.map = map;
    }

    public AtlasTexture getMap()
    {
        return map;
    }

    /**
     * Fired when the TextureMap is told to refresh it's stitched texture.
     * Called before the {@link net.minecraft.client.renderer.texture.TextureAtlasSprite} are loaded.
     * Contains the set of {@link ResourceLocation}s that will load TextureAtlasSprites.
     *
     * To load new textures to be stitched into the AtlasTexture add to the resourceLocations Set.
     */
    public static class Pre extends TextureStitchEvent
    {
        private final Set<ResourceLocation> resourceLocations;

        public Pre(AtlasTexture map, Set<ResourceLocation> resourceLocations)
        {
            super(map);
            this.resourceLocations = resourceLocations;
        }

        public Set<ResourceLocation> getResourceLocations() {
            return resourceLocations;
        }
    }

    /**
     * This event is fired once the texture map has loaded all textures and
     * stitched them together. All Icons should have there locations defined
     * by the time this is fired.
     */
    public static class Post extends TextureStitchEvent
    {
        public Post(AtlasTexture map){ super(map); }
    }
}
