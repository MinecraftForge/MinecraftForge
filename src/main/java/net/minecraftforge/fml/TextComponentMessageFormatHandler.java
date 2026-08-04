/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class TextComponentMessageFormatHandler {
    public static int handle(final TranslationTextComponent parent, final List<ITextComponent> children, final Object[] formatArgs, final String format) {
        try {
            StringTextComponent component = new StringTextComponent(ForgeI18n.parseFormat(format, formatArgs));
            component.getStyle().setParentStyle(parent.getStyle());
            children.add(component);
            return format.length();
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }
}
