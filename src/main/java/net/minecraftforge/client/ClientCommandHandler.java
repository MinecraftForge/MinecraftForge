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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ClientCommandHandler
{
    private static CommandDispatcher<CommandSourceStack> commands = null;

    /*
     * For internal use
     * 
     * Merges command dispatcher use for suggestions to the command dispatcher used for client commands so they can be sent to the server, and vice versa so client commands appear
     * with server commands in suggestions
     */
    public static void mergeServerCommands(RootCommandNode<SharedSuggestionProvider> serverCommands)
    {
        commands = new CommandDispatcher<>();
        MinecraftForge.EVENT_BUS.post(new RegisterClientCommandsEvent(commands));
        RootCommandNode<SharedSuggestionProvider> serverCommandsCopy = new RootCommandNode<>();
        mergeCommandNode(serverCommands, serverCommandsCopy, new HashMap<>(), Minecraft.getInstance().getConnection().getSuggestionsProvider(),
                (suggestions) -> 0, (suggestions) -> null);

        mergeCommandNode(commands.getRoot(), serverCommands, new HashMap<>(), getSource(), (suggestions) -> 0, (client) -> {
            SuggestionProvider<SharedSuggestionProvider> suggestionProvider = SuggestionProviders
                    .safelySwap((SuggestionProvider<SharedSuggestionProvider>) (SuggestionProvider<?>) client);
            if (suggestionProvider == SuggestionProviders.ASK_SERVER)
            {
                suggestionProvider = (context, builder) -> {
                    ClientCommandSource source = getSource();
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

        mergeCommandNode(serverCommandsCopy, commands.getRoot(), new HashMap<>(), Minecraft.getInstance().getConnection().getSuggestionsProvider(),
                (source) -> {
                    Minecraft.getInstance().player.chat((source.getInput().startsWith("/") ? "" : "/") + source.getInput());
                    return 0;
                }, (suggestions) -> null);
    }

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

    private static <S, T> CommandNode<T> toResult(CommandNode<S> sourceNode, Map<CommandNode<S>, CommandNode<T>> sourceToResult, S canUse, Command<T> execute,
            Function<SuggestionProvider<S>, SuggestionProvider<T>> sourceToResultSuggestion)
    {
        if (!sourceToResult.containsKey(sourceNode))
        {
            ArgumentBuilder<T, ?> resultBuilder = null;
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
        else
        {
            return sourceToResult.get(sourceNode);
        }
        
    }

    /**
     * Always try to execute the cached parsing of client message as a command. Requires that the execute field of the commands to be set to send to server so that they aren't
     * treated as client command's that do nothing.
     * 
     * {@link net.minecraft.command.Commands#handleCommand(net.minecraft.command.CommandSource,String)} for reference
     * 
     * @param currentParse
     *            current state of the parser for the message
     * @return false leaves the message to be sent to the server, true means it should be caught before {@link net.minecraft.client.gui.screen.ChatScreen#func_231161_c_(String)}
     */
    public static boolean sendMessage(String sendMessage)
    {
        ClientCommandSource source = getSource();

        StringReader reader = new StringReader(sendMessage);

        if (!reader.canRead() || reader.read() != '/')
        {
            return false;
        }

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
            Minecraft.getInstance().player.sendMessage(
                    new TextComponent("").append(ComponentUtils.fromMessage(syntax.getRawMessage())).withStyle(ChatFormatting.RED), Util.NIL_UUID);
            if (syntax.getInput() != null && syntax.getCursor() >= 0)
            {
                int position = Math.min(syntax.getInput().length(), syntax.getCursor());
                MutableComponent details = new TextComponent("").withStyle(ChatFormatting.GRAY);
                details.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, reader.getString()));
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
            Component component = new TranslatableComponent("command.failed");
            component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, message));
            Minecraft.getInstance().player.sendMessage(new TextComponent("").append(component).withStyle(ChatFormatting.RED), Util.NIL_UUID);
            generic.printStackTrace();
        }
        return true;
    }

    private static ClientCommandSource getSource()
    {
        LocalPlayer player = Minecraft.getInstance().player;
        return new ClientCommandSource(player, player.position(), player.getRotationVector(), player.getPermissionLevel(),
                player.getName().getString(), player.getDisplayName(), player);
    }
}
