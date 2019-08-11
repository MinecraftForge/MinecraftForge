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
import java.util.List;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.extensions.IForgeAtlasTexture;
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
     * Custom sprites added here will be evaluated in parallel with normal sprites.
     * In case of id conflict custom sprites will have priority.
     */
    public static class Pre extends TextureStitchEvent
    {
        private final IResourceManager resourceManager;
        private final Set<ResourceLocation> sprites;
        private final List<IForgeAtlasTexture.SpriteProvider> customSprites;

        public Pre(AtlasTexture map, IResourceManager resourceManager, Set<ResourceLocation> sprites, List<IForgeAtlasTexture.SpriteProvider> customSprites)
        {
            super(map);
            this.resourceManager = resourceManager;
            this.sprites = sprites;
            this.customSprites = customSprites;
        }

        public boolean addSprite(ResourceLocation sprite)
        {
            return sprites.add(sprite);
        }

        public void addSprite(final IForgeAtlasTexture.SpriteProvider sprite)
        {
            customSprites.add(sprite);
        }

        public IResourceManager getResourceManager()
        {
            return resourceManager;
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
