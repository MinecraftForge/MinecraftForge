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

package net.minecraftforge.common.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.util.text.ITextComponent;

/**
 * Implementations of this interface can be registered using {@link EntitySelectorManager#register}
 */
public interface IEntitySelectorType
{
    /**
     * Returns an {@link EntitySelector} based on the given {@link EntitySelectorParser}. <br>
     *
     * Use {@link EntitySelectorParser#getReader} to read extra arguments and {@link EntitySelectorParser#addFilter} to add the corresponding filters. <br>
     * If the token being parsed does not match the syntax of this selector, this method should throw an appropriate {@link CommandSyntaxException}.
     */
    EntitySelector build(EntitySelectorParser parser) throws CommandSyntaxException;

    /**
     * Returns an {@link ITextComponent} containing a short description for this selector type.
     */
    ITextComponent getSuggestionTooltip();
}
