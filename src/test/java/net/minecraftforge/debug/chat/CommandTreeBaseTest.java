/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.server.command.CommandNode;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.CommandTreeHelp;

@Mod(modid = CommandTreeBaseTest.MOD_ID, name = "CommandTreeBaseTest", version = "1.0.0", acceptableRemoteVersions = "*")
public class CommandTreeBaseTest
{
    public static final String MOD_ID = "command_tree_base_test";

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandTreeTest());
    }

    public static class CommandTreeTest extends CommandTreeBase
    {
        public CommandTreeTest()
        {
            addSubcommand(new CommandPing(this));
            addSubcommand(new CommandValue(this));
            addSubcommand(new CommandTreeHelp(this));
        }

        @Override
        public String getName()
        {
            return "treecommand_test";
        }

        public static class CommandPing extends CommandNode
        {
            public CommandPing(ICommand parent)
            {
                super(parent);
            }

            @Override
            public String getName()
            {
                return "ping";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                sender.sendMessage(new TextComponentTranslation("commands.treecommand_test.ping.text"));
            }
        }

        public static class CommandValue extends CommandTreeBase
        {
            private static int value = 0;

            public CommandValue(ICommand parent)
            {
                super(parent);
                addSubcommand(new CommandSet(this));
                addSubcommand(new CommandGet(this));
                addSubcommand(new CommandTreeHelp(this));
            }

            @Override
            public String getName()
            {
                return "value";
            }

            public static class CommandSet extends CommandNode
            {
                public CommandSet(ICommand parent)
                {
                    super(parent);
                }

                @Override
                public String getName()
                {
                    return "set";
                }

                @Override
                public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
                {
                    value = CommandTreeTest.parseInt(args[0]);
                    sender.sendMessage(new TextComponentTranslation("commands.treecommand_test.value.set.text", value));
                }
            }

            public static class CommandGet extends CommandNode
            {
                public CommandGet(ICommand parent)
                {
                    super(parent);
                }

                @Override
                public String getName()
                {
                    return "get";
                }

                @Override
                public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
                {
                    sender.sendMessage(new TextComponentTranslation("commands.treecommand_test.value.set.text", value));
                }
            }
        }
    }
}
