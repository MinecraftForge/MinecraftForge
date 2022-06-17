/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import java.util.Map;
import java.util.function.Function;

/**
 * Internal utility class for various command-related operations.
 *
 * <strong>For internal Forge use only.</strong>
 *
 * @hidden
 */
public final class CommandHelper
{
    private CommandHelper()
    {
    }

    /**
     * Deep copies the children of a command node and stores a link between the source and the copy
     *
     * @param sourceNode               the original command node
     * @param resultNode               the result command node
     * @param sourceToResult           a map storing the original command node as the key and the result command node as the value
     * @param canUse                   used to check if the player can use the command
     * @param execute                  the command to execute in place of the old command
     * @param sourceToResultSuggestion a function to convert from the {@link SuggestionProvider} with the original source stack to the {@link SuggestionProvider} with the result source stack
     */
    public static <S, T> void mergeCommandNode(CommandNode<S> sourceNode, CommandNode<T> resultNode, Map<CommandNode<S>, CommandNode<T>> sourceToResult,
                                               S canUse, Command<T> execute, Function<SuggestionProvider<S>, SuggestionProvider<T>> sourceToResultSuggestion)
    {
        sourceToResult.put(sourceNode, resultNode);
        for (CommandNode<S> sourceChild : sourceNode.getChildren())
        {
            if (sourceChild.canUse(canUse))
            {
                resultNode.addChild(toResult(sourceChild, sourceToResult, canUse, execute, sourceToResultSuggestion));
            }
        }
    }

    /**
     * Creates a deep copy of a command node with a different source stack
     *
     * @param sourceNode               the original command node
     * @param sourceToResult           a map storing the original command node as the key and the result command node as the value
     * @param canUse                   used to check if the player can use the command
     * @param execute                  the command to execute in place of the old command
     * @param sourceToResultSuggestion a function to convert from the {@link SuggestionProvider} with the original source stack to the {@link SuggestionProvider} with the result source stack
     * @return the deep copied command node with the new source stack
     */
    private static <S, T> CommandNode<T> toResult(CommandNode<S> sourceNode, Map<CommandNode<S>, CommandNode<T>> sourceToResult, S canUse, Command<T> execute,
                                                  Function<SuggestionProvider<S>, SuggestionProvider<T>> sourceToResultSuggestion)
    {
        if (sourceToResult.containsKey(sourceNode))
            return sourceToResult.get(sourceNode);

        ArgumentBuilder<T, ?> resultBuilder;
        if (sourceNode instanceof ArgumentCommandNode<?, ?>)
        {
            ArgumentCommandNode<S, ?> sourceArgument = (ArgumentCommandNode<S, ?>) sourceNode;
            RequiredArgumentBuilder<T, ?> resultArgumentBuilder = RequiredArgumentBuilder.argument(sourceArgument.getName(), sourceArgument.getType());
            if (sourceArgument.getCustomSuggestions() != null)
            {
                resultArgumentBuilder.suggests(sourceToResultSuggestion.apply(sourceArgument.getCustomSuggestions()));
            }
            resultBuilder = resultArgumentBuilder;
        } else if (sourceNode instanceof LiteralCommandNode<?>)
        {
            LiteralCommandNode<S> sourceLiteral = (LiteralCommandNode<S>) sourceNode;
            resultBuilder = LiteralArgumentBuilder.literal(sourceLiteral.getLiteral());
        } else if (sourceNode instanceof RootCommandNode<?>)
        {
            CommandNode<T> resultNode = new RootCommandNode<>();
            mergeCommandNode(sourceNode, resultNode, sourceToResult, canUse, execute, sourceToResultSuggestion);
            return resultNode;
        } else
        {
            throw new IllegalStateException("Node type " + sourceNode + " is not a standard node type");
        }

        if (sourceNode.getCommand() != null)
        {
            resultBuilder.executes(execute);
        }

        if (sourceNode.getRedirect() != null)
        {
            resultBuilder.redirect(toResult(sourceNode.getRedirect(), sourceToResult, canUse, execute, sourceToResultSuggestion));
        }

        CommandNode<T> resultNode = resultBuilder.build();
        mergeCommandNode(sourceNode, resultNode, sourceToResult, canUse, execute, sourceToResultSuggestion);
        return resultNode;
    }
}
