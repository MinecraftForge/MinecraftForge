/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fmllegacy;

import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class TextComponentMessageFormatHandler {
    public static int handle(final TranslatableComponent parent, final List<FormattedText> children, final Object[] formatArgs, final String format) {
        try {
            final String formattedString = ForgeI18n.parseFormat(format, formatArgs);

            // See MinecraftForge/MinecraftForge#7396
            if (format.indexOf('\'') != -1) {
                final boolean onlyMissingQuotes = format.chars()
                        .filter(ch -> formattedString.indexOf((char) ch) == -1)
                        .allMatch(ch -> ch == '\'');
                if (onlyMissingQuotes) {
                    return 0;
                }
            }

            TextComponent component = new TextComponent(formattedString);
            component.getStyle().applyTo(parent.getStyle());
            children.add(component);
            return format.length();
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }
}
