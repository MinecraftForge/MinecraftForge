/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml;

import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class TextComponentMessageFormatHandler {
    public static int handle(final TranslationTextComponent parent, final List<ITextProperties> children, final Object[] formatArgs, final String format) {
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

            StringTextComponent component = new StringTextComponent(formattedString);
            component.getStyle().applyTo(parent.getStyle());
            children.add(component);
            return format.length();
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }
}
