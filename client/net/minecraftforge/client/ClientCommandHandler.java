package net.minecraftforge.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import cpw.mods.fml.client.FMLClientHandler;
import static net.minecraft.util.EnumChatFormatting.*;

/**
 * The class that handles client-side chat commands. You should register any
 * commands that you want handled on the client with this command handler.
 * 
 * If there is a command with the same name registered both on the server and
 * client, the client takes precedence!
 * 
 */
public class ClientCommandHandler extends CommandHandler
{
    public static final ClientCommandHandler instance = new ClientCommandHandler();

    public String[] latestAutoComplete = null;

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

        String[] temp = message.split(" ");
        String[] args = new String[temp.length - 1];
        String commandName = temp[0];
        System.arraycopy(temp, 1, args, 0, args.length);
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
                sender.sendChatToPlayer(format("commands.generic.permission").setColor(RED));
            }
        }
        catch (WrongUsageException wue)
        {
            sender.sendChatToPlayer(format("commands.generic.usage", format(wue.getMessage(), wue.getErrorOjbects())).setColor(RED));
        }
        catch (CommandException ce)
        {
            sender.sendChatToPlayer(format(ce.getMessage(), ce.getErrorOjbects()).setColor(RED));
        }
        catch (Throwable t)
        {
            sender.sendChatToPlayer(format("commands.generic.exception").setColor(RED));
            t.printStackTrace();
        }

        return 0;
    }

    //Couple of helpers because the mcp names are stupid and long...
    private ChatMessageComponent format(String str, Object... args)
    {
        return ChatMessageComponent.createFromTranslationWithSubstitutions(str, args);
    }

    private ChatMessageComponent format(String str)
    {
        return ChatMessageComponent.createFromTranslationKey(str);
    }

    public void autoComplete(String leftOfCursor, String full)
    {
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
                    if (leftOfCursor.indexOf(' ') == -1)
                    {
                        for (int i = 0; i < commands.size(); i++)
                        {
                            commands.set(i, GRAY + "/" + commands.get(i) + RESET);
                        }
                    }
                    else
                    {
                        for (int i = 0; i < commands.size(); i++)
                        {
                            commands.set(i, GRAY + commands.get(i) + RESET);
                        }
                    }

                    latestAutoComplete = commands.toArray(new String[commands.size()]);
                }
            }
        }
    }
}