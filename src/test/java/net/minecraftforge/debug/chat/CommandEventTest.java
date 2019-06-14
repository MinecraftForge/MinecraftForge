/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.debug.chat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

//@Mod("command_event_test")
public class CommandEventTest
{
    public final Logger LOGGER = LogManager.getLogger();

    public CommandEventTest()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCommand(CommandEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getParseResults().getContext().getDispatcher();
        List<CommandNode<CommandSource>> nodes = new ArrayList<>(event.getParseResults().getContext().getNodes().keySet());
        CommandSource source = event.getParseResults().getContext().getSource();

        // test: when the /time command is used with no arguments, automatically add default arguments (/time set day)
        if (nodes.size() == 1 && nodes.get(0) == dispatcher.getRoot().getChild("time"))
        {
            event.setParseResults(dispatcher.parse("time set day", source));
            return;
        }

        // test: whenever a player uses the /give command, let everyone on the server know
        if (nodes.size() > 0 && nodes.get(0) == dispatcher.getRoot().getChild("give"))
        {
            String msg = source.getName() + " used the give command: " + event.getParseResults().getReader().getString();
            source.getServer().getPlayerList().getPlayers().forEach(entityPlayerMP -> entityPlayerMP.sendMessage(new TextComponentString(msg)));
            return;
        }

        // test: when the /kill command is used with no arguments, throw a custom exception
        if (nodes.size() == 1 && nodes.get(0) == dispatcher.getRoot().getChild("kill"))
        {
            event.setException(new CommandException(new TextComponentString("You tried to use the /kill command with no arguments")));
            event.setCanceled(true);
            return;
        }
    }

}
