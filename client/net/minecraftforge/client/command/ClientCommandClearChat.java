package net.minecraftforge.client.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import cpw.mods.fml.client.FMLClientHandler;

public class ClientCommandClearChat extends CommandBase
{

    @Override
    public String getCommandName()
    {
        return "clearchat";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "/clearchat";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring)
    {
        FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().clearChatMessages();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1iCommandSender)
    {
        return true;
    }
}
