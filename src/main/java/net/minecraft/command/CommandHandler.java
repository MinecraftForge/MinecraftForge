package net.minecraft.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;

public class CommandHandler implements ICommandManager
{
    private static final Logger field_147175_a = LogManager.getLogger();
    // JAVADOC FIELD $$ field_71562_a
    private final Map commandMap = new HashMap();
    // JAVADOC FIELD $$ field_71561_b
    private final Set commandSet = new HashSet();
    private static final String __OBFID = "CL_00001765";

    public int executeCommand(ICommandSender par1ICommandSender, String par2Str)
    {
        par2Str = par2Str.trim();

        if (par2Str.startsWith("/"))
        {
            par2Str = par2Str.substring(1);
        }

        String[] astring = par2Str.split(" ");
        String s1 = astring[0];
        astring = dropFirstString(astring);
        ICommand icommand = (ICommand)this.commandMap.get(s1);
        int i = this.getUsernameIndex(icommand, astring);
        int j = 0;
        ChatComponentTranslation chatcomponenttranslation;

        try
        {
            if (icommand == null)
            {
                throw new CommandNotFoundException();
            }

            if (icommand.canCommandSenderUseCommand(par1ICommandSender))
            {
                CommandEvent event = new CommandEvent(icommand, par1ICommandSender, astring);
                if (MinecraftForge.EVENT_BUS.post(event))
                {
                    if (event.exception != null)
                    {
                        throw event.exception;
                    }
                    return 1;
                }

                if (i > -1)
                {
                    EntityPlayerMP[] aentityplayermp = PlayerSelector.matchPlayers(par1ICommandSender, astring[i]);
                    String s2 = astring[i];
                    EntityPlayerMP[] aentityplayermp1 = aentityplayermp;
                    int k = aentityplayermp.length;

                    for (int l = 0; l < k; ++l)
                    {
                        EntityPlayerMP entityplayermp = aentityplayermp1[l];
                        astring[i] = entityplayermp.getCommandSenderName();

                        try
                        {
                            icommand.processCommand(par1ICommandSender, astring);
                            ++j;
                        }
                        catch (CommandException commandexception)
                        {
                            ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation(commandexception.getMessage(), commandexception.getErrorOjbects());
                            chatcomponenttranslation1.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                            par1ICommandSender.func_145747_a(chatcomponenttranslation1);
                        }
                    }

                    astring[i] = s2;
                }
                else
                {
                    icommand.processCommand(par1ICommandSender, astring);
                    ++j;
                }
            }
            else
            {
                ChatComponentTranslation chatcomponenttranslation2 = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
                chatcomponenttranslation2.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                par1ICommandSender.func_145747_a(chatcomponenttranslation2);
            }
        }
        catch (WrongUsageException wrongusageexception)
        {
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.usage", new Object[] {new ChatComponentTranslation(wrongusageexception.getMessage(), wrongusageexception.getErrorOjbects())});
            chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            par1ICommandSender.func_145747_a(chatcomponenttranslation);
        }
        catch (CommandException commandexception1)
        {
            chatcomponenttranslation = new ChatComponentTranslation(commandexception1.getMessage(), commandexception1.getErrorOjbects());
            chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            par1ICommandSender.func_145747_a(chatcomponenttranslation);
        }
        catch (Throwable throwable)
        {
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.exception", new Object[0]);
            chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            par1ICommandSender.func_145747_a(chatcomponenttranslation);
            field_147175_a.error("Couldn\'t process command", throwable);
        }

        return j;
    }

    // JAVADOC METHOD $$ func_71560_a
    public ICommand registerCommand(ICommand par1ICommand)
    {
        List list = par1ICommand.getCommandAliases();
        this.commandMap.put(par1ICommand.getCommandName(), par1ICommand);
        this.commandSet.add(par1ICommand);

        if (list != null)
        {
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();
                ICommand icommand1 = (ICommand)this.commandMap.get(s);

                if (icommand1 == null || !icommand1.getCommandName().equals(s))
                {
                    this.commandMap.put(s, par1ICommand);
                }
            }
        }

        return par1ICommand;
    }

    // JAVADOC METHOD $$ func_71559_a
    private static String[] dropFirstString(String[] par0ArrayOfStr)
    {
        String[] astring1 = new String[par0ArrayOfStr.length - 1];

        for (int i = 1; i < par0ArrayOfStr.length; ++i)
        {
            astring1[i - 1] = par0ArrayOfStr[i];
        }

        return astring1;
    }

    // JAVADOC METHOD $$ func_71558_b
    public List getPossibleCommands(ICommandSender par1ICommandSender, String par2Str)
    {
        String[] astring = par2Str.split(" ", -1);
        String s1 = astring[0];

        if (astring.length == 1)
        {
            ArrayList arraylist = new ArrayList();
            Iterator iterator = this.commandMap.entrySet().iterator();

            while (iterator.hasNext())
            {
                Entry entry = (Entry)iterator.next();

                if (CommandBase.doesStringStartWith(s1, (String)entry.getKey()) && ((ICommand)entry.getValue()).canCommandSenderUseCommand(par1ICommandSender))
                {
                    arraylist.add(entry.getKey());
                }
            }

            return arraylist;
        }
        else
        {
            if (astring.length > 1)
            {
                ICommand icommand = (ICommand)this.commandMap.get(s1);

                if (icommand != null)
                {
                    return icommand.addTabCompletionOptions(par1ICommandSender, dropFirstString(astring));
                }
            }

            return null;
        }
    }

    // JAVADOC METHOD $$ func_71557_a
    public List getPossibleCommands(ICommandSender par1ICommandSender)
    {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = this.commandSet.iterator();

        while (iterator.hasNext())
        {
            ICommand icommand = (ICommand)iterator.next();

            if (icommand.canCommandSenderUseCommand(par1ICommandSender))
            {
                arraylist.add(icommand);
            }
        }

        return arraylist;
    }

    // JAVADOC METHOD $$ func_71555_a
    public Map getCommands()
    {
        return this.commandMap;
    }

    // JAVADOC METHOD $$ func_82370_a
    private int getUsernameIndex(ICommand par1ICommand, String[] par2ArrayOfStr)
    {
        if (par1ICommand == null)
        {
            return -1;
        }
        else
        {
            for (int i = 0; i < par2ArrayOfStr.length; ++i)
            {
                if (par1ICommand.isUsernameIndex(par2ArrayOfStr, i) && PlayerSelector.matchesMultiplePlayers(par2ArrayOfStr[i]))
                {
                    return i;
                }
            }

            return -1;
        }
    }
}