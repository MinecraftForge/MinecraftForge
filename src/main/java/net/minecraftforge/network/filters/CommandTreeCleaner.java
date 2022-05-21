/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.filters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

class CommandTreeCleaner
{

    /**
     * Cleans the command tree starting at the given root node from any argument types that do not match the given predicate.
     * Any {@code ArgumentCommandNode}s that have an unmatched argument type will be stripped from the tree.
     * @return A new command tree, stripped of any unmatched argument types
     */
    public static <S> RootCommandNode<S> cleanArgumentTypes(RootCommandNode<S> root, Predicate<ArgumentType<?>> argumentTypeFilter)
    {
        Predicate<CommandNode<?>> nodeFilter = node -> !(node instanceof ArgumentCommandNode<?, ?>) || argumentTypeFilter.test(((ArgumentCommandNode<?, ?>)node).getType());
        return (RootCommandNode<S>)processCommandNode(root, nodeFilter, new HashMap<>());
    }

    private static <S> CommandNode<S> processCommandNode(CommandNode<S> node, Predicate<CommandNode<?>> nodeFilter, Map<CommandNode<S>, CommandNode<S>> newNodes)
    {
        CommandNode<S> existingNode = newNodes.get(node);
        if (existingNode == null)
        {
            CommandNode<S> newNode = cloneNode(node, nodeFilter, newNodes);
            newNodes.put(node, newNode);
            node.getChildren().stream()
                    .filter(nodeFilter)
                    .map(child -> processCommandNode(child, nodeFilter, newNodes))
                    .forEach(newNode::addChild);
            return newNode;
        }
        else
        {
            return existingNode;
        }
    }

    private static <S> CommandNode<S> cloneNode(CommandNode<S> node, Predicate<CommandNode<?>> nodeFilter, Map<CommandNode<S>, CommandNode<S>> newNodes)
    {
        if (node instanceof RootCommandNode<?>)
        {
            return new RootCommandNode<>();
        }
        else
        {
            ArgumentBuilder<S, ?> builder = node.createBuilder();
            if (node.getRedirect() != null)
            {
                if (nodeFilter.test(node.getRedirect()))
                {
                    builder.forward(processCommandNode(node.getRedirect(), nodeFilter, newNodes), node.getRedirectModifier(), node.isFork());
                }
                else
                {
                    builder.redirect(null);
                }
            }
            return builder.build();
        }
    }

}
