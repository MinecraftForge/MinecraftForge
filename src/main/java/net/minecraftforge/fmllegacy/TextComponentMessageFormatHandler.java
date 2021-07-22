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

package net.minecraftforge.fmllegacy;

import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class TextComponentMessageFormatHandler {
    public static int handle(final TranslatableComponent parent, final List<FormattedText> children, final Object[] formatArgs, final String format) {
        try {
            TextComponent component = new TextComponent(ForgeI18n.parseFormat(format, formatArgs));
            component.getStyle().applyTo(parent.getStyle());
            children.add(component);
            return format.length();
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }
}
