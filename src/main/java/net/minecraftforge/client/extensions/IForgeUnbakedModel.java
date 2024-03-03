/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraftforge.common.model.animation.IClip;

import java.util.Optional;

public interface IForgeUnbakedModel
{
    /**
     * Retrieves information about an animation clip in the model.
     * @param name The clip name
     * @return
     */
    default Optional<? extends IClip> getClip(String name) {
        return Optional.empty();
    }
}
