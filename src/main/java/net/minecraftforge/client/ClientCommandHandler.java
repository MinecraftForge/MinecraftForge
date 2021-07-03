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
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ClientCommandHandler
{
    private static CommandDispatcher<CommandSource> commands = null;

    public static void mergeServerCommands(RootCommandNode<ISuggestionProvider> serverCommands)
    {
        commands = new CommandDispatcher<>();
        MinecraftForge.EVENT_BUS.post(new RegisterClientCommandsEvent(commands));
        RootCommandNode<ISuggestionProvider> serverCommandsCopy = new RootCommandNode<>();
        mergeCommandNode(serverCommands, serverCommandsCopy, new HashMap<>(), null, (suggestions) -> 0, (suggestions) -> null);

        ClientPlayerEntity player = Minecraft.getInstance().player;
        mergeCommandNode(commands.getRoot(), serverCommands, new HashMap<>(), new ClientCommandSource(player, player.position(), player.getRotationVector(),
                player.getPermissionLevel(), player.getName().getString(), player.getDisplayName(), player), (suggestions) -> 0,
                (client) -> SuggestionProviders.safelySwap((SuggestionProvider<ISuggestionProvider>) (SuggestionProvider<?>) client));

        mergeCommandNode(serverCommandsCopy, commands.getRoot(), new HashMap<>(), null, (source) -> {
            Minecraft.getInstance().player.chat(source.getInput());
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
        ClientPlayerEntity player = Minecraft.getInstance().player;
        ClientCommandSource source = new ClientCommandSource(player, player.position(), player.getRotationVector(), player.getPermissionLevel(),
                player.getName().getString(), player.getDisplayName(), player);

        StringReader reader = new StringReader(sendMessage);

        if (!reader.canRead() || reader.read() != '/')
        {
            return false;
        }

        try
        {
            commands.execute(reader, source);
        }
        catch (CommandException execution)// Probably thrown by the command
        {
            Minecraft.getInstance().player.sendMessage(new StringTextComponent("").append(execution.getComponent()).withStyle(TextFormatting.RED),
                    Util.NIL_UUID);
        }
        catch (CommandSyntaxException syntax)// Usually thrown by the CommandDispatcher
        {
            Minecraft.getInstance().player.sendMessage(
                    new StringTextComponent("").append(TextComponentUtils.fromMessage(syntax.getRawMessage())).withStyle(TextFormatting.RED), Util.NIL_UUID);
            if (syntax.getInput() != null && syntax.getCursor() >= 0)
            {
                int position = Math.min(syntax.getInput().length(), syntax.getCursor());
                IFormattableTextComponent details = new StringTextComponent("").withStyle(TextFormatting.GRAY);
                details.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, reader.getString()));
                if (position > 10)
                {
                    details.append("...");
                }
                details.append(syntax.getInput().substring(Math.max(0, position - 10), position));
                if (position < syntax.getInput().length())
                {
                    details.append(new StringTextComponent(syntax.getInput().substring(position)).withStyle(TextFormatting.RED, TextFormatting.UNDERLINE));
                }
                details.append(new TranslationTextComponent("command.context.here").withStyle(TextFormatting.RED, TextFormatting.ITALIC));
                Minecraft.getInstance().player.sendMessage(new StringTextComponent("").append(details).withStyle(TextFormatting.RED), Util.NIL_UUID);
            }
        }
        catch (Exception generic)// Probably thrown by the command
        {
            StringTextComponent message = new StringTextComponent(generic.getMessage() == null ? generic.getClass().getName() : generic.getMessage());
            ITextComponent component = new TranslationTextComponent("command.failed");
            component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, message));
            Minecraft.getInstance().player.sendMessage(new StringTextComponent("").append(component).withStyle(TextFormatting.RED), Util.NIL_UUID);
        }
        return true;
    }

    public static CompletableFuture<Suggestions> getSuggestions(String command, CompletableFuture<Suggestions> serverSuggestions)
    {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        ClientCommandSource source = new ClientCommandSource(player, player.position(), player.getRotationVector(), player.getPermissionLevel(),
                player.getName().getString(), player.getDisplayName(), player);

        StringReader reader = new StringReader(command);

        if (!reader.canRead() || reader.read() != '/')
        {
            return Suggestions.empty();
        }

        ParseResults<CommandSource> parse = commands.parse(reader, source);
        return commands.getCompletionSuggestions(parse).thenCombine(serverSuggestions,
                (client, server) -> Suggestions.merge(command, Arrays.asList(client, server)));
    }
}
