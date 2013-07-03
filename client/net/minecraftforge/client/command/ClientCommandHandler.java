package net.minecraftforge.client.command;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import cpw.mods.fml.client.FMLClientHandler;

/**
 * The class that handles client-side chat commands. You should register any
 * commands that you want handled on the client with this command handler.
 * 
 * If there is a command with the same name registered both on the server and
 * client, the client takes precedence!
 * 
 * @author Lepko
 * 
 */
public class ClientCommandHandler extends CommandHandler
{

    public ClientCommandHandler()
    {
        this.registerCommand(new ClientCommandClearChat());
    }

    /**
     * @return 1 if successfully executed, 0 if wrong usage, it doesn't exist or
     *         it was canceled.
     */
    @Override
    public int executeCommand(ICommandSender sender, String message)
    {
        message = message.trim();

        if (message.startsWith("/"))
        {
            message = message.substring(1);
        }

        String[] args = message.split(" ");
        String commandName = args[0];
        System.arraycopy(args, 1, args, 0, args.length - 1);
        ICommand icommand = (ICommand) getCommands().get(commandName);

        try
        {
            if (icommand == null)
            {
                return 0;
            }

            if (icommand.canCommandSenderUseCommand(sender))
            {
                CommandEvent event = new CommandEvent(icommand, sender, args);
                if (MinecraftForge.EVENT_BUS.post(event))
                {
                    if (event.exception != null)
                    {
                        throw event.exception;
                    }
                    return 0;
                }

                icommand.processCommand(sender, args);
                return 1;
            }
            else
            {
                sender.func_110122_a(ChatMessageComponent.func_111077_e("commands.generic.permission").func_111059_a(EnumChatFormatting.RED));
            }
        }
        catch (WrongUsageException wue)
        {
            sender.func_110122_a(ChatMessageComponent.func_111082_b("commands.generic.usage", new Object[] { ChatMessageComponent.func_111082_b(wue.getMessage(), wue.getErrorOjbects()) }).func_111059_a(EnumChatFormatting.RED));
        }
        catch (CommandException ce)
        {
            sender.func_110122_a(ChatMessageComponent.func_111082_b(ce.getMessage(), ce.getErrorOjbects()).func_111059_a(EnumChatFormatting.RED));
        }
        catch (Throwable t)
        {
            sender.func_110122_a(ChatMessageComponent.func_111077_e("commands.generic.exception").func_111059_a(EnumChatFormatting.RED));
            t.printStackTrace();
        }

        return 0;
    }

    public void autoComplete(String leftOfCursor, String full)
    {
        System.out.println("par1: " + leftOfCursor + " == par2: " + full + " Thread=" + Thread.currentThread().getName());

        latestAutoComplete = null;

        if (leftOfCursor.charAt(0) == '/')
        {
            leftOfCursor = leftOfCursor.substring(1);

            Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.currentScreen instanceof GuiChat)
            {
                List<String> commands = getPossibleCommands(mc.thePlayer, leftOfCursor);
                if (commands != null && !commands.isEmpty())
                {
                    for (int i = 0; i < commands.size(); i++)
                    {
                        commands.set(i, EnumChatFormatting.GRAY + "/" + commands.get(i));
                    }
                    latestAutoComplete = commands.toArray(new String[commands.size()]);
                }
            }
        }
    }

    public String[] latestAutoComplete = null;
}
