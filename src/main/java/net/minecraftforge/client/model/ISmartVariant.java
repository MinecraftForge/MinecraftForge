/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import net.minecraft.client.renderer.model.IUnbakedModel;

@Deprecated
public interface ISmartVariant
{
    default IUnbakedModel process(IUnbakedModel base) {
        return base;
    }
}
