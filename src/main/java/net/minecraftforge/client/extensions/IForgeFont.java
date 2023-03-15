/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;

/**
 * Extension interface for {@link Font}.
 */
public interface IForgeFont
{
    FormattedText ELLIPSIS = FormattedText.of("...");

    Font self();

    /**
     * If the width of the text exceeds {@code maxWidth}, an ellipse is added and the text is substringed.
     *
     * @param text     the text to ellipsize if needed
     * @param maxWidth the maximum width of the text
     * @return the ellipsized text
     */
    default FormattedText ellipsize(FormattedText text, int maxWidth)
    {
        final Font self = self();
        final int strWidth = self.width(text);
        final int ellipsisWidth = self.width(ELLIPSIS);
        if (strWidth > maxWidth)
        {
            if (ellipsisWidth >= maxWidth) return self.substrByWidth(text, maxWidth);
            return FormattedText.composite(
                    self.substrByWidth(text, maxWidth - ellipsisWidth),
                    ELLIPSIS
            );
        }
        return text;
    }
}
