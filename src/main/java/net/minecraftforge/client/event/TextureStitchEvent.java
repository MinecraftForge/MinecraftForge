/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Set;


/**
 * Fired before and after a texture atlas stitched together.
 * See the two subclasses to listen for before and after stitching.
 *
 * @see TextureStitchEvent.Pre
 * @see TextureStitchEvent.Post
 * @see TextureAtlas
 */
public class TextureStitchEvent extends Event implements IModBusEvent
{
    private final TextureAtlas atlas;

    /**
     * @hidden
     */
    public TextureStitchEvent(TextureAtlas atlas)
    {
        this.atlas = atlas;
    }

    /**
     * {@return the texture atlas}
     */
    public TextureAtlas getAtlas()
    {
        return atlas;
    }

    /**
     * <p>Fired <b>before</b> a texture atlas is stitched together.
     * This can be used to add custom sprites to be stitched into the atlas. </p>
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus()} mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class Pre extends TextureStitchEvent
    {
        private final Set<ResourceLocation> sprites;

        /**
         * @hidden
         * @see ForgeHooksClient#onTextureStitchedPre(TextureAtlas, Set)
         */
        public Pre(TextureAtlas map, Set<ResourceLocation> sprites)
        {
            super(map);
            this.sprites = sprites;
        }

        /**
         * Adds a sprite to be stitched into the texture atlas.
         *
         * <p>Callers should check that the atlas which the event is fired for is the atlas they wish to stitch the
         * sprite into, as otherwise they would be stitching the sprite into all atlases.</p>
         *
         * @param sprite the location of the sprite
         */
        public boolean addSprite(ResourceLocation sprite) {
            return this.sprites.add(sprite);
        }
    }

    /**
     * Fired <b>after</b> a texture atlas is stitched together and all textures therein has been loaded.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus()} mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class Post extends TextureStitchEvent
    {
        /**
         * @hidden
         * @see ForgeHooksClient#onTextureStitchedPost(TextureAtlas)
         */
        public Post(TextureAtlas map){ super(map); }
    }
}
