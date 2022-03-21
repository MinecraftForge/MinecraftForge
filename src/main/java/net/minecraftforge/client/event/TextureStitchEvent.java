/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Set;


public class TextureStitchEvent extends Event implements IModBusEvent
{
    private final TextureAtlas atlas;

    public TextureStitchEvent(TextureAtlas atlas)
    {
        this.atlas = atlas;
    }

    public TextureAtlas getAtlas()
    {
        return atlas;
    }

    /**
     * Fired when the TextureMap is told to refresh it's stitched texture.
     * Called before the {@link net.minecraft.client.renderer.texture.TextureAtlasSprite} are loaded.
     */
    public static class Pre extends TextureStitchEvent
    {
        private final Set<ResourceLocation> sprites;

        public Pre(TextureAtlas map, Set<ResourceLocation> sprites)
        {
            super(map);
            this.sprites = sprites;
        }

        /**
         * Add a sprite to be stitched into the Texture Atlas.
         */
        public boolean addSprite(ResourceLocation sprite) {
            return this.sprites.add(sprite);
        }
    }

    /**
     * This event is fired once the texture map has loaded all textures and
     * stitched them together. All Icons should have there locations defined
     * by the time this is fired.
     */
    public static class Post extends TextureStitchEvent
    {
        public Post(TextureAtlas map){ super(map); }
    }
}
