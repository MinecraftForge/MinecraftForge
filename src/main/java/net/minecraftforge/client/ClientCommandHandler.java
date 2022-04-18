/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public class ClientCommandHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static CommandDispatcher<CommandSourceStack> commands = null;

    public static void init()
    {
        MinecraftForge.EVENT_BUS.addListener(ClientCommandHandler::handleClientPlayerLogin);
    }

    private static void handleClientPlayerLogin(ClientPlayerNetworkEvent.LoggedInEvent event)
    {
        // some custom server implementations do not send ClientboundCommandsPacket, provide a fallback
        var suggestionDispatcher = mergeServerCommands(new CommandDispatcher<>());
        if (event.getConnection().getPacketListener() instanceof ClientPacketListener listener)
        {
            // Must set this, so that suggestions for client-only commands work, if server never sends commands packet
            listener.commands = suggestionDispatcher;
        }
    }

    /*
     * For internal use
     * 
     * Merges command dispatcher use for suggestions to the command dispatcher used for client commands so they can be sent to the server, and vice versa so client commands appear
     * with server commands in suggestions
     */
    public static CommandDispatcher<SharedSuggestionProvider> mergeServerCommands(CommandDispatcher<SharedSuggestionProvider> serverCommands)
    {
        CommandDispatcher<CommandSourceStack> commandsTemp = new CommandDispatcher<>();
        MinecraftForge.EVENT_BUS.post(new RegisterClientCommandsEvent(commandsTemp));

        // Copies the client commands into another RootCommandNode so that redirects can't be used with server commands
        commands = new CommandDispatcher<>();
        copy(commandsTemp.getRoot(), commands.getRoot());

        // Copies the server commands into another RootCommandNode so that redirects can't be used with client commands
        RootCommandNode<SharedSuggestionProvider> serverCommandsRoot = serverCommands.getRoot();
        CommandDispatcher<SharedSuggestionProvider> newServerCommands = new CommandDispatcher<>();
        copy(serverCommandsRoot, newServerCommands.getRoot());

        // Copies the server side commands into a temporary server side commands root node to be used later without the client commands
        RootCommandNode<SharedSuggestionProvider> serverCommandsCopy = new RootCommandNode<>();
        mergeCommandNode(newServerCommands.getRoot(), serverCommandsCopy, new IdentityHashMap<>(),
                Minecraft.getInstance().getConnection().getSuggestionsProvider(), (context) -> 0, (suggestions) -> null);

        // Copies the client side commands into the server side commands to be used for suggestions
        mergeCommandNode(commands.getRoot(), newServerCommands.getRoot(), new IdentityHashMap<>(), getSource(), (context) -> 0, (suggestions) -> {
            SuggestionProvider<SharedSuggestionProvider> suggestionProvider = SuggestionProviders
                    .safelySwap((SuggestionProvider<SharedSuggestionProvider>) (SuggestionProvider<?>) suggestions);
            if (suggestionProvider == SuggestionProviders.ASK_SERVER)
            {
                suggestionProvider = (context, builder) -> {
                    ClientCommandSourceStack source = getSource();
                    StringReader reader = new StringReader(context.getInput());
                    if (reader.canRead() && reader.peek() == '/')
                    {
                        reader.skip();
                    }

                    ParseResults<CommandSourceStack> parse = commands.parse(reader, source);
                    return commands.getCompletionSuggestions(parse);
                };
            }
            return suggestionProvider;
        });

        // Copies the server side commands into the client side commands so that they can be sent to the server as a chat message
        mergeCommandNode(serverCommandsCopy, commands.getRoot(), new IdentityHashMap<>(), Minecraft.getInstance().getConnection().getSuggestionsProvider(),
                (context) -> {
                    Minecraft.getInstance().player.chat((context.getInput().startsWith("/") ? "" : "/") + context.getInput());
                    return 0;
                }, (suggestions) -> null);
        return newServerCommands;
    }

    /**
     * @return The command dispatcher for client side commands
     */
    public static CommandDispatcher<CommandSourceStack> getDispatcher()
    {
        return commands;
    }

    /**
     * @return A {@link ClientCommandSourceStack} for the player in the current client
     */
    public static ClientCommandSourceStack getSource()
    {
        LocalPlayer player = Minecraft.getInstance().player;
        return new ClientCommandSourceStack(player, player.position(), player.getRotationVector(), player.getPermissionLevel(),
                player.getName().getString(), player.getDisplayName(), player);
    }

    /**
     * 
     * Creates a deep copy of the sourceNode while keeping the redirects referring to the old command tree
     * 
     * @param sourceNode
     *            the original
     * @param resultNode
     *            the result
     */
    private static <S> void copy(CommandNode<S> sourceNode, CommandNode<S> resultNode)
    {
        Map<CommandNode<S>, CommandNode<S>> newNodes = new IdentityHashMap<>();
        newNodes.put(sourceNode, resultNode);
        for (CommandNode<S> child : sourceNode.getChildren())
        {
            CommandNode<S> copy = newNodes.computeIfAbsent(child, innerChild ->
            {
                ArgumentBuilder<S, ?> builder = innerChild.createBuilder();
                CommandNode<S> innerCopy = builder.build();
                copy(innerChild, innerCopy);
                return innerCopy;
            });
            resultNode.addChild(copy);
        }
    }

    /**
     * 
     * Deep copies the children of a command node and stores a link between the source and the copy
     * 
     * @param sourceNode
     *            the original command node
     * @param resultNode
     *            the result command node
     * @param sourceToResult
     *            a map storing the original command node as the key and the result command node as the value
     * @param canUse
     *            used to check if the player can use the command
     * @param execute
     *            the command to execute in place of the old command
     * @param sourceToResultSuggestion
     *            a function to convert from the {@link SuggestionProvider} with the original source stack to the {@link SuggestionProvider} with the result source stack
     */
    private static <S, T> void mergeCommandNode(CommandNode<S> sourceNode, CommandNode<T> resultNode, Map<CommandNode<S>, CommandNode<T>> sourceToResult,
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
     * 
     * Creates a deep copy of a command node with a different source stack
     * 
     * @param sourceNode
     *            the original command node
     * @param sourceToResult
     *            a map storing the original command node as the key and the result command node as the value
     * @param canUse
     *            used to check if the player can use the command
     * @param execute
     *            the command to execute in place of the old command
     * @param sourceToResultSuggestion
     *            a function to convert from the {@link SuggestionProvider} with the original source stack to the {@link SuggestionProvider} with the result source stack
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
        }
        else if (sourceNode instanceof LiteralCommandNode<?>)
        {
            LiteralCommandNode<S> sourceLiteral = (LiteralCommandNode<S>) sourceNode;
            resultBuilder = LiteralArgumentBuilder.literal(sourceLiteral.getLiteral());
        }
        else if (sourceNode instanceof RootCommandNode<?>)
        {
            CommandNode<T> resultNode = new RootCommandNode<>();
            mergeCommandNode(sourceNode, resultNode, sourceToResult, canUse, execute, sourceToResultSuggestion);
            return resultNode;
        }
        else
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

    /**
     * Always try to execute the cached parsing of client message as a command. Requires that the execute field of the commands to be set to send to server so that they aren't
     * treated as client command's that do nothing.
     * 
     * {@link net.minecraft.commands.Commands#performCommand(CommandSourceStack, String)} for reference
     * 
     * @param sendMessage
     *            the chat message
     * @return false leaves the message to be sent to the server, true means it should be caught before {@link LocalPlayer#chat(String)}
     */
    public static boolean sendMessage(String sendMessage)
    {
        StringReader reader = new StringReader(sendMessage);

        if (!reader.canRead() || reader.read() != '/')
        {
            return false;
        }

        ClientCommandSourceStack source = getSource();

        try
        {
            commands.execute(reader, source);
        }
        catch (CommandRuntimeException execution)// Probably thrown by the command
        {
            Minecraft.getInstance().player.sendMessage(new TextComponent("").append(execution.getComponent()).withStyle(ChatFormatting.RED), Util.NIL_UUID);
        }
        catch (CommandSyntaxException syntax)// Usually thrown by the CommandDispatcher
        {
            if (syntax.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand())
            {
                // in case of unknown command, let the server try and handle it
                return false;
            }
            Minecraft.getInstance().player.sendMessage(
                    new TextComponent("").append(ComponentUtils.fromMessage(syntax.getRawMessage())).withStyle(ChatFormatting.RED), Util.NIL_UUID);
            if (syntax.getInput() != null && syntax.getCursor() >= 0)
            {
                int position = Math.min(syntax.getInput().length(), syntax.getCursor());
                MutableComponent details = new TextComponent("")
                        .withStyle(ChatFormatting.GRAY)
                        .withStyle((style) -> style
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, reader.getString())));
                if (position > 10)
                {
                    details.append("...");
                }
                details.append(syntax.getInput().substring(Math.max(0, position - 10), position));
                if (position < syntax.getInput().length())
                {
                    details.append(new TextComponent(syntax.getInput().substring(position)).withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE));
                }
                details.append(new TranslatableComponent("command.context.here").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
                Minecraft.getInstance().player.sendMessage(new TextComponent("").append(details).withStyle(ChatFormatting.RED), Util.NIL_UUID);
            }
        }
        catch (Exception generic)// Probably thrown by the command
        {
            TextComponent message = new TextComponent(generic.getMessage() == null ? generic.getClass().getName() : generic.getMessage());
            Minecraft.getInstance().player.sendMessage(new TranslatableComponent("command.failed")
                    .withStyle(ChatFormatting.RED)
                    .withStyle((style) -> style
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, message))),
                    Util.NIL_UUID);
            LOGGER.error("Error executing client command \"{}\"", sendMessage, generic);
        }
        return true;
    }
}
