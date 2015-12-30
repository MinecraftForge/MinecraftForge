package net.minecraftforge.test;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameData;

@Mod(modid="clientcommandtest", name="Client Command Test", version="0.0.0")
public class ClientCommandTest {
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ClientCommandHandler.instance.registerCommand(new TestCommand());
    }

    private class TestCommand extends CommandBase {
        @Override
        public String getCommandName()
        {
            return "clientCommandTest";
        }

        @Override
        public String getCommandUsage(ICommandSender sender)
        {
            return "clientCommandTest <block>";
        }

        @Override
        public boolean canCommandSenderUseCommand(ICommandSender sender)
        {
            return true;
        }

        @Override
        public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
        {
            if (args.length > 0)
            {
                return getListOfStringsMatchingLastWord(args, GameData.getBlockRegistry().getKeys());
            }

            return null;
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) throws CommandException
        {
            if (args.length > 0)
            {
                sender.addChatMessage(new ChatComponentText("Input: " + Arrays.toString(args)));
            }
            else
            {
                sender.addChatMessage(new ChatComponentText("No arguments."));
            }
        }
    }
}
