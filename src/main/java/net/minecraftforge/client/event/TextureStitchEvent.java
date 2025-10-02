/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Fired after a texture atlas is stitched together.
 *
 * @see TextureStitchEvent.Post
 * @see TextureAtlas
 */
@NullMarked
public sealed interface TextureStitchEvent {
    /**
     * {@return the texture atlas}
     */
    TextureAtlas getAtlas();

    // Use atlas info JSON files instead
    // /**
    //  * <p>Fired <b>before</b> a texture atlas is stitched together.
    //  * This can be used to add custom sprites to be stitched into the atlas.</p>
    //  *
    //  * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
    //  */
    // public static class Pre extends TextureStitchEvent {
    //     private final Set<ResourceLocation> sprites;
    //
    //     @ApiStatus.Internal
    //     public Pre(TextureAtlas map, Set<ResourceLocation> sprites) {
    //         super(map);
    //         this.sprites = sprites;
    //     }
    //
    //     /**
    //      * Adds a sprite to be stitched into the texture atlas.
    //      *
    //      * <p>Callers should check that the atlas which the event is fired for is the atlas they wish to stitch the
    //      * sprite into, as otherwise they would be stitching the sprite into all atlases.</p>
    //      *
    //      * @param sprite the location of the sprite
    //      */
    //     public boolean addSprite(ResourceLocation sprite) {
    //         return this.sprites.add(sprite);
    //     }
    // }

    /**
     * Fired <b>after</b> a texture atlas is stitched together and all textures therein has been loaded.
     *
     * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    record Post(TextureAtlas getAtlas) implements RecordEvent, TextureStitchEvent {
        public static final EventBus<Post> BUS = EventBus.create(Post.class);

        @Deprecated(forRemoval = true, since = "1.21.9")
        public static EventBus<Post> getBus(BusGroup modBusGroup) {
            return BUS;
        }

        @ApiStatus.Internal
        public Post {}
    }
}
