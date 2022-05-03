/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.chat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.ParsedCommandNode;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod("command_event_test")
@Mod.EventBusSubscriber
public class CommandEventTest
{
    public final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onCommand(CommandEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getParseResults().getContext().getDispatcher();
        List<ParsedCommandNode<CommandSource>> nodes = event.getParseResults().getContext().getNodes();
        CommandSource source = event.getParseResults().getContext().getSource();

        // test: when the /time command is used with no arguments, automatically add default arguments (/time set day)
        if (nodes.size() == 1 && nodes.get(0).getNode() == dispatcher.getRoot().getChild("time"))
        {
            event.setParseResults(dispatcher.parse("time set day", source));
            return;
        }

        // test: whenever a player uses the /give command, let everyone on the server know
        if (nodes.size() > 0 && nodes.get(0).getNode() == dispatcher.getRoot().getChild("give"))
        {
            String msg = source.getTextName() + " used the give command: " + event.getParseResults().getReader().getString();
            source.getServer().getPlayerList().getPlayers().forEach(player -> player.sendMessage(new StringTextComponent(msg), player.getUUID()));
            return;
        }

        // test: when the /kill command is used with no arguments, throw a custom exception
        if (nodes.size() == 1 && nodes.get(0).getNode() == dispatcher.getRoot().getChild("kill"))
        {
            event.setException(new CommandException(new StringTextComponent("You tried to use the /kill command with no arguments")));
            event.setCanceled(true);
            return;
        }
    }

}
