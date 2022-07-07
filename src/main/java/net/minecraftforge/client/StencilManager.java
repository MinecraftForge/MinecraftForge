/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import java.util.BitSet;

public final class StencilManager
{
    private static final BitSet BITS = new BitSet(8);

    /**
     * Reserve a stencil bit for use in rendering
     * <p>
     * Note: you must check the {@link com.mojang.blaze3d.pipeline.RenderTarget} you are working with to
     * determine if stencil bits are enabled on it before use.
     *
     * @return A bit, or -1 if no further stencil bits are available
     */
    public static int reserveBit()
    {
        int bit = BITS.nextClearBit(0);
        if (bit >= 0)
            BITS.set(bit);
        return bit;
    }

    /**
     * Release the stencil bit for other use
     *
     * @param bit The bit obtained from {@link #reserveBit()}
     */
    public static void releaseBit(int bit)
    {
        if (bit >= 0 && bit < BITS.length())
            BITS.clear(bit);
    }

    private StencilManager()
    {
    }
}
