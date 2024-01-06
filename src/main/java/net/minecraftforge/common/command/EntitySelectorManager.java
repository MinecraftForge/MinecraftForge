/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Allows modders to register custom entity selectors by assigning an {@link IEntitySelectorType} to a String token. <br>
 * The token "test", for example, corresponds to @test[...] in a command.
 */
public class EntitySelectorManager
{
    private static final HashMap<String, IEntitySelectorType> REGISTRY = new HashMap<>();

    /**
     * Registers a new {@link IEntitySelectorType} for the given {@code token}.<br>
     *
     * @param token Defines the name of the selector
     */
    public static void register(String token, IEntitySelectorType type)
    {
        if (token.isEmpty())
        {
            throw new IllegalArgumentException("Token must not be empty");
        }

        if (Arrays.asList("p", "a", "r", "s", "e").contains(token))
        {
            throw new IllegalArgumentException("Token clashes with vanilla @" + token);
        }

        for (char c : token.toCharArray())
        {
            if (!StringReader.isAllowedInUnquotedString(c)) {
                throw new IllegalArgumentException("Token must only contain allowed characters");
            }
        }

        REGISTRY.put(token, type);
    }

    /**
     * This method is called in {@link EntitySelectorParser#parse()} <br>
     *
     * If the REGISTRY does not contain a custom selector for the command being parsed,
     * this method returns {@code null} and the vanilla logic in {@code EntitySelectorParser#parseSelector()} is used.
     */
    public static EntitySelector parseSelector(EntitySelectorParser parser) throws CommandSyntaxException
    {
        if (parser.getReader().canRead())
        {
            int i = parser.getReader().getCursor();
            String token = parser.getReader().readUnquotedString();
            IEntitySelectorType type = REGISTRY.get(token);
            if (type != null)
            {
                return type.build(parser);
            }

            parser.getReader().setCursor(i);
        }

        return null;
    }

    /**
     * This method is called in {@code EntitySelectorParser#fillSelectorSuggestions(SuggestionsBuilder)}
     */
    public static void fillSelectorSuggestions(SuggestionsBuilder suggestionBuilder)
    {
        REGISTRY.forEach((token, type) -> suggestionBuilder.suggest("@" + token, type.getSuggestionTooltip()));
    }
}
