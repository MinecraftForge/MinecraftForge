/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.server.command.CommandTreeBase;

@Mod(modid = CommandTreeBaseTest.MOD_ID, name = "CommandTreeBaseTest", version = "1.0.0", acceptableRemoteVersions = "*")
public class CommandTreeBaseTest
{
    public static final String MOD_ID = "command_tree_base_test";

    @Mod.Instance(CommandTreeBaseTest.MOD_ID)
    public static CommandTreeBaseTest inst;

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandTreeTest());
    }

    public static class CommandTreeTest extends CommandTreeBase
    {
        public CommandTreeTest()
        {
            addSubcommand(new CommandPing());
            addSubcommand(new CommandValue());
        }

        @Override
        public String getName()
        {
            return "treecommand_test";
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return "commands.treecommand_test.usage";
        }

        public static class CommandPing extends CommandBase
        {
            @Override
            public String getName()
            {
                return "ping";
            }

            @Override
            public String getUsage(ICommandSender sender)
            {
                return "commands.treecommand_test.ping.usage";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                sender.sendMessage(new TextComponentString("Pong!"));
            }
        }

        public static class CommandValue extends CommandTreeBase
        {
            private static int value = 0;

            public CommandValue()
            {
                addSubcommand(new CommandSet());
                addSubcommand(new CommandGet());
            }

            @Override
            public String getName()
            {
                return "value";
            }

            @Override
            public String getUsage(ICommandSender sender)
            {
                return "commands.treecommand_test.value.usage";
            }

            public static class CommandSet extends CommandBase
            {
                @Override
                public String getName()
                {
                    return "set";
                }

                @Override
                public String getUsage(ICommandSender sender)
                {
                    return "commands.treecommand_test.value.set.usage";
                }

                @Override
                public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
                {
                    value = CommandTreeTest.parseInt(args[0]);
                    sender.sendMessage(new TextComponentString("Test value set to: " + value));
                }
            }

            public static class CommandGet extends CommandBase
            {
                @Override
                public String getName()
                {
                    return "get";
                }

                @Override
                public String getUsage(ICommandSender sender)
                {
                    return "commands.treecommand_test.value.get.usage";
                }

                @Override
                public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
                {
                    sender.sendMessage(new TextComponentString("Test value: " + value));
                }
            }
        }
    }
}
