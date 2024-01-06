/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

/**
 * Implementations of this interface can be registered using {@link EntitySelectorManager#register}
 */
public interface IEntitySelectorType
{
    /**
     * Returns an {@link EntitySelector} based on the given {@link EntitySelectorParser}. <br>
     *
     * Use {@link EntitySelectorParser#getReader} to read extra arguments and {@link EntitySelectorParser#addPredicate(Predicate)} to add the corresponding filters. <br>
     * If the token being parsed does not match the syntax of this selector, this method should throw an appropriate {@link CommandSyntaxException}.
     */
    EntitySelector build(EntitySelectorParser parser) throws CommandSyntaxException;

    /**
     * Returns an {@link Component} containing a short description for this selector type.
     */
    Component getSuggestionTooltip();
}
