package net.minecraft.command;

import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public abstract class CommandBase implements ICommand
{
    private static IAdminCommand theAdmin;
    private static final String __OBFID = "CL_00001739";

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

    public List getCommandAliases()
    {
        return null;
    }

    // JAVADOC METHOD $$ func_71519_b
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return par1ICommandSender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

    // JAVADOC METHOD $$ func_71516_a
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return null;
    }

    // JAVADOC METHOD $$ func_71526_a
    public static int parseInt(ICommandSender par0ICommandSender, String par1Str)
    {
        try
        {
            return Integer.parseInt(par1Str);
        }
        catch (NumberFormatException numberformatexception)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {par1Str});
        }
    }

    // JAVADOC METHOD $$ func_71528_a
    public static int parseIntWithMin(ICommandSender par0ICommandSender, String par1Str, int par2)
    {
        // JAVADOC METHOD $$ func_71532_a
        return parseIntBounded(par0ICommandSender, par1Str, par2, Integer.MAX_VALUE);
    }

    // JAVADOC METHOD $$ func_71532_a
    public static int parseIntBounded(ICommandSender par0ICommandSender, String par1Str, int par2, int par3)
    {
        int k = parseInt(par0ICommandSender, par1Str);

        if (k < par2)
        {
            throw new NumberInvalidException("commands.generic.num.tooSmall", new Object[] {Integer.valueOf(k), Integer.valueOf(par2)});
        }
        else if (k > par3)
        {
            throw new NumberInvalidException("commands.generic.num.tooBig", new Object[] {Integer.valueOf(k), Integer.valueOf(par3)});
        }
        else
        {
            return k;
        }
    }

    // JAVADOC METHOD $$ func_82363_b
    public static double parseDouble(ICommandSender par0ICommandSender, String par1Str)
    {
        try
        {
            double d0 = Double.parseDouble(par1Str);

            if (!Doubles.isFinite(d0))
            {
                throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {par1Str});
            }
            else
            {
                return d0;
            }
        }
        catch (NumberFormatException numberformatexception)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {par1Str});
        }
    }

    public static double func_110664_a(ICommandSender par0ICommandSender, String par1Str, double par2)
    {
        return func_110661_a(par0ICommandSender, par1Str, par2, Double.MAX_VALUE);
    }

    public static double func_110661_a(ICommandSender par0ICommandSender, String par1Str, double par2, double par4)
    {
        double d2 = parseDouble(par0ICommandSender, par1Str);

        if (d2 < par2)
        {
            throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d2), Double.valueOf(par2)});
        }
        else if (d2 > par4)
        {
            throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d2), Double.valueOf(par4)});
        }
        else
        {
            return d2;
        }
    }

    public static boolean func_110662_c(ICommandSender par0ICommandSender, String par1Str)
    {
        if (!par1Str.equals("true") && !par1Str.equals("1"))
        {
            if (!par1Str.equals("false") && !par1Str.equals("0"))
            {
                throw new CommandException("commands.generic.boolean.invalid", new Object[] {par1Str});
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    // JAVADOC METHOD $$ func_71521_c
    public static EntityPlayerMP getCommandSenderAsPlayer(ICommandSender par0ICommandSender)
    {
        if (par0ICommandSender instanceof EntityPlayerMP)
        {
            return (EntityPlayerMP)par0ICommandSender;
        }
        else
        {
            throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.", new Object[0]);
        }
    }

    public static EntityPlayerMP getPlayer(ICommandSender par0ICommandSender, String par1Str)
    {
        EntityPlayerMP entityplayermp = PlayerSelector.matchOnePlayer(par0ICommandSender, par1Str);

        if (entityplayermp != null)
        {
            return entityplayermp;
        }
        else
        {
            entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(par1Str);

            if (entityplayermp == null)
            {
                throw new PlayerNotFoundException();
            }
            else
            {
                return entityplayermp;
            }
        }
    }

    public static String func_96332_d(ICommandSender par0ICommandSender, String par1Str)
    {
        EntityPlayerMP entityplayermp = PlayerSelector.matchOnePlayer(par0ICommandSender, par1Str);

        if (entityplayermp != null)
        {
            return entityplayermp.getCommandSenderName();
        }
        else if (PlayerSelector.hasArguments(par1Str))
        {
            throw new PlayerNotFoundException();
        }
        else
        {
            return par1Str;
        }
    }

    public static IChatComponent func_147178_a(ICommandSender p_147178_0_, String[] p_147178_1_, int p_147178_2_)
    {
        return func_147176_a(p_147178_0_, p_147178_1_, p_147178_2_, false);
    }

    public static IChatComponent func_147176_a(ICommandSender p_147176_0_, String[] p_147176_1_, int p_147176_2_, boolean p_147176_3_)
    {
        ChatComponentText chatcomponenttext = new ChatComponentText("");

        for (int j = p_147176_2_; j < p_147176_1_.length; ++j)
        {
            if (j > p_147176_2_)
            {
                chatcomponenttext.func_150258_a(" ");
            }

            Object object = new ChatComponentText(p_147176_1_[j]);

            if (p_147176_3_)
            {
                IChatComponent ichatcomponent = PlayerSelector.func_150869_b(p_147176_0_, p_147176_1_[j]);

                if (ichatcomponent != null)
                {
                    object = ichatcomponent;
                }
                else if (PlayerSelector.hasArguments(p_147176_1_[j]))
                {
                    throw new PlayerNotFoundException();
                }
            }

            chatcomponenttext.func_150257_a((IChatComponent)object);
        }

        return chatcomponenttext;
    }

    public static String func_82360_a(ICommandSender par0ICommandSender, String[] par1ArrayOfStr, int par2)
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (int j = par2; j < par1ArrayOfStr.length; ++j)
        {
            if (j > par2)
            {
                stringbuilder.append(" ");
            }

            String s = par1ArrayOfStr[j];
            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    public static double func_110666_a(ICommandSender par0ICommandSender, double par1, String par3Str)
    {
        return func_110665_a(par0ICommandSender, par1, par3Str, -30000000, 30000000);
    }

    public static double func_110665_a(ICommandSender par0ICommandSender, double par1, String par3Str, int par4, int par5)
    {
        boolean flag = par3Str.startsWith("~");

        if (flag && Double.isNaN(par1))
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {Double.valueOf(par1)});
        }
        else
        {
            double d1 = flag ? par1 : 0.0D;

            if (!flag || par3Str.length() > 1)
            {
                boolean flag1 = par3Str.contains(".");

                if (flag)
                {
                    par3Str = par3Str.substring(1);
                }

                d1 += parseDouble(par0ICommandSender, par3Str);

                if (!flag1 && !flag)
                {
                    d1 += 0.5D;
                }
            }

            if (par4 != 0 || par5 != 0)
            {
                if (d1 < (double)par4)
                {
                    throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d1), Integer.valueOf(par4)});
                }

                if (d1 > (double)par5)
                {
                    throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d1), Integer.valueOf(par5)});
                }
            }

            return d1;
        }
    }

    public static Item func_147179_f(ICommandSender p_147179_0_, String p_147179_1_)
    {
        Item item = (Item)Item.field_150901_e.getObject(p_147179_1_);

        if (item == null)
        {
            try
            {
                Item item1 = Item.func_150899_d(Integer.parseInt(p_147179_1_));

                if (item1 != null)
                {
                    ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.generic.deprecatedId", new Object[] {Item.field_150901_e.func_148750_c(item1)});
                    chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.GRAY);
                    p_147179_0_.func_145747_a(chatcomponenttranslation);
                }

                item = item1;
            }
            catch (NumberFormatException numberformatexception)
            {
                ;
            }
        }

        if (item == null)
        {
            throw new NumberInvalidException("commands.give.notFound", new Object[] {p_147179_1_});
        }
        else
        {
            return item;
        }
    }

    public static Block func_147180_g(ICommandSender p_147180_0_, String p_147180_1_)
    {
        if (Block.field_149771_c.func_148741_d(p_147180_1_))
        {
            return (Block)Block.field_149771_c.getObject(p_147180_1_);
        }
        else
        {
            try
            {
                int i = Integer.parseInt(p_147180_1_);

                if (Block.field_149771_c.func_148753_b(i))
                {
                    Block block = Block.func_149729_e(i);
                    ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.generic.deprecatedId", new Object[] {Block.field_149771_c.func_148750_c(block)});
                    chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.GRAY);
                    p_147180_0_.func_145747_a(chatcomponenttranslation);
                    return block;
                }
            }
            catch (NumberFormatException numberformatexception)
            {
                ;
            }

            throw new NumberInvalidException("commands.give.notFound", new Object[] {p_147180_1_});
        }
    }

    // JAVADOC METHOD $$ func_71527_a
    public static String joinNiceString(Object[] par0ArrayOfObj)
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < par0ArrayOfObj.length; ++i)
        {
            String s = par0ArrayOfObj[i].toString();

            if (i > 0)
            {
                if (i == par0ArrayOfObj.length - 1)
                {
                    stringbuilder.append(" and ");
                }
                else
                {
                    stringbuilder.append(", ");
                }
            }

            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    public static IChatComponent func_147177_a(IChatComponent[] p_147177_0_)
    {
        ChatComponentText chatcomponenttext = new ChatComponentText("");

        for (int i = 0; i < p_147177_0_.length; ++i)
        {
            if (i > 0)
            {
                if (i == p_147177_0_.length - 1)
                {
                    chatcomponenttext.func_150258_a(" and ");
                }
                else if (i > 0)
                {
                    chatcomponenttext.func_150258_a(", ");
                }
            }

            chatcomponenttext.func_150257_a(p_147177_0_[i]);
        }

        return chatcomponenttext;
    }

    public static String func_96333_a(Collection par0Collection)
    {
        // JAVADOC METHOD $$ func_71527_a
        return joinNiceString(par0Collection.toArray(new String[par0Collection.size()]));
    }

    // JAVADOC METHOD $$ func_71523_a
    public static boolean doesStringStartWith(String par0Str, String par1Str)
    {
        return par1Str.regionMatches(true, 0, par0Str, 0, par0Str.length());
    }

    // JAVADOC METHOD $$ func_71530_a
    public static List getListOfStringsMatchingLastWord(String[] par0ArrayOfStr, String ... par1ArrayOfStr)
    {
        String s1 = par0ArrayOfStr[par0ArrayOfStr.length - 1];
        ArrayList arraylist = new ArrayList();
        String[] astring1 = par1ArrayOfStr;
        int i = par1ArrayOfStr.length;

        for (int j = 0; j < i; ++j)
        {
            String s2 = astring1[j];

            if (doesStringStartWith(s1, s2))
            {
                arraylist.add(s2);
            }
        }

        return arraylist;
    }

    // JAVADOC METHOD $$ func_71531_a
    public static List getListOfStringsFromIterableMatchingLastWord(String[] par0ArrayOfStr, Iterable par1Iterable)
    {
        String s = par0ArrayOfStr[par0ArrayOfStr.length - 1];
        ArrayList arraylist = new ArrayList();
        Iterator iterator = par1Iterable.iterator();

        while (iterator.hasNext())
        {
            String s1 = (String)iterator.next();

            if (doesStringStartWith(s, s1))
            {
                arraylist.add(s1);
            }
        }

        return arraylist;
    }

    // JAVADOC METHOD $$ func_82358_a
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return false;
    }

    public static void notifyAdmins(ICommandSender par0ICommandSender, String par1Str, Object ... par2ArrayOfObj)
    {
        notifyAdmins(par0ICommandSender, 0, par1Str, par2ArrayOfObj);
    }

    public static void notifyAdmins(ICommandSender par0ICommandSender, int par1, String par2Str, Object ... par3ArrayOfObj)
    {
        if (theAdmin != null)
        {
            theAdmin.notifyAdmins(par0ICommandSender, par1, par2Str, par3ArrayOfObj);
        }
    }

    // JAVADOC METHOD $$ func_71529_a
    public static void setAdminCommander(IAdminCommand par0IAdminCommand)
    {
        theAdmin = par0IAdminCommand;
    }

    public int compareTo(ICommand par1ICommand)
    {
        return this.getCommandName().compareTo(par1ICommand.getCommandName());
    }

    public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand)par1Obj);
    }
}