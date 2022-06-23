/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.Minecraft;

import java.util.BitSet;
import java.util.Locale;

public class MinecraftForgeClient
{
    public static float getPartialTick()
    {
        return ForgeHooksClient.partialTick;
    }

    /**
     * Returns the Locale set by the player in Minecraft.
     * Useful for creating string and number formatters.
     */
    public static Locale getLocale()
    {
        return Minecraft.getInstance().getLanguageManager().getSelected().getJavaLocale();
    }

    private static final BitSet stencilBits = new BitSet(8);

    static
    {
        stencilBits.set(0, 8);
    }

    /**
     * Reserve a stencil bit for use in rendering
     * <p>
     * Note: you must check the {@link com.mojang.blaze3d.pipeline.RenderTarget} you are working with to
     * determine if stencil bits are enabled on it before use.
     *
     * @return A bit, or -1 if no further stencil bits are available
     */
    public static int reserveStencilBit()
    {
        int bit = stencilBits.nextSetBit(0);
        if (bit >= 0)
        {
            stencilBits.clear(bit);
        }
        return bit;
    }

    /**
     * Release the stencil bit for other use
     *
     * @param bit The bit obtained from {@link #reserveStencilBit()}
     */
    public static void releaseStencilBit(int bit)
    {
        if (bit >= 0 && bit < stencilBits.length())
        {
            stencilBits.set(bit);
        }
    }
}
