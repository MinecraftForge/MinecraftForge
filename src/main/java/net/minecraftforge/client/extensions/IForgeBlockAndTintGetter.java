/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;

/**
 * Extension interface for {@link BlockAndTintGetter}.
 */
public interface IForgeBlockAndTintGetter
{
    private BlockAndTintGetter self()
    {
        return (BlockAndTintGetter) this;
    }

    /**
     * Computes the shade for a given normal.
     * Alternate version of the vanilla method taking in a {@link Direction}.
     */
    default float getShade(float normalX, float normalY, float normalZ, boolean shade)
    {
        return self().getShade(Direction.getNearest(normalX, normalY, normalZ), shade);
    }
}
