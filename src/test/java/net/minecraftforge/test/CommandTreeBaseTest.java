package net.minecraftforge.test;

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
        public String getCommandName()
        {
            return "treecommand_test";
        }

        @Override
        public String getCommandUsage(ICommandSender sender)
        {
            return "commands.treecommand_test.usage";
        }

        public static class CommandPing extends CommandBase
        {
            @Override
            public String getCommandName()
            {
                return "ping";
            }

            @Override
            public String getCommandUsage(ICommandSender sender)
            {
                return "commands.treecommand_test.ping.usage";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                sender.addChatMessage(new TextComponentString("Pong!"));
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
            public String getCommandName()
            {
                return "value";
            }

            @Override
            public String getCommandUsage(ICommandSender sender)
            {
                return "commands.treecommand_test.value.usage";
            }

            public static class CommandSet extends CommandBase
            {
                @Override
                public String getCommandName()
                {
                    return "set";
                }

                @Override
                public String getCommandUsage(ICommandSender sender)
                {
                    return "commands.treecommand_test.value.set.usage";
                }

                @Override
                public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
                {
                    value = CommandTreeTest.parseInt(args[0]);
                    sender.addChatMessage(new TextComponentString("Test value set to: " + value));
                }
            }

            public static class CommandGet extends CommandBase
            {
                @Override
                public String getCommandName()
                {
                    return "get";
                }

                @Override
                public String getCommandUsage(ICommandSender sender)
                {
                    return "commands.treecommand_test.value.get.usage";
                }

                @Override
                public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
                {
                    sender.addChatMessage(new TextComponentString("Test value: " + value));
                }
            }
        }
    }
}
