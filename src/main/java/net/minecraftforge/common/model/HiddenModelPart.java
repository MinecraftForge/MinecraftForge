/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.model;

import com.google.common.collect.ImmutableList;

final class HiddenModelPart
{
    private final ImmutableList<String> path;

    HiddenModelPart(ImmutableList<String> path)
    {
        this.path = path;
    }

    ImmutableList<String> getPath()
    {
        return path;
    }
}
