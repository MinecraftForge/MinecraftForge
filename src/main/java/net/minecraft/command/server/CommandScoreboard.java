package net.minecraft.command.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandScoreboard extends CommandBase
{
    private static final String __OBFID = "CL_00000896";

    public String getCommandName()
    {
        return "scoreboard";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.scoreboard.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length >= 1)
        {
            if (par2ArrayOfStr[0].equalsIgnoreCase("objectives"))
            {
                if (par2ArrayOfStr.length == 1)
                {
                    throw new WrongUsageException("commands.scoreboard.objectives.usage", new Object[0]);
                }

                if (par2ArrayOfStr[1].equalsIgnoreCase("list"))
                {
                    this.func_147196_d(par1ICommandSender);
                }
                else if (par2ArrayOfStr[1].equalsIgnoreCase("add"))
                {
                    if (par2ArrayOfStr.length < 4)
                    {
                        throw new WrongUsageException("commands.scoreboard.objectives.add.usage", new Object[0]);
                    }

                    this.func_147193_c(par1ICommandSender, par2ArrayOfStr, 2);
                }
                else if (par2ArrayOfStr[1].equalsIgnoreCase("remove"))
                {
                    if (par2ArrayOfStr.length != 3)
                    {
                        throw new WrongUsageException("commands.scoreboard.objectives.remove.usage", new Object[0]);
                    }

                    this.func_147191_h(par1ICommandSender, par2ArrayOfStr[2]);
                }
                else
                {
                    if (!par2ArrayOfStr[1].equalsIgnoreCase("setdisplay"))
                    {
                        throw new WrongUsageException("commands.scoreboard.objectives.usage", new Object[0]);
                    }

                    if (par2ArrayOfStr.length != 3 && par2ArrayOfStr.length != 4)
                    {
                        throw new WrongUsageException("commands.scoreboard.objectives.setdisplay.usage", new Object[0]);
                    }

                    this.func_147198_k(par1ICommandSender, par2ArrayOfStr, 2);
                }

                return;
            }

            if (par2ArrayOfStr[0].equalsIgnoreCase("players"))
            {
                if (par2ArrayOfStr.length == 1)
                {
                    throw new WrongUsageException("commands.scoreboard.players.usage", new Object[0]);
                }

                if (par2ArrayOfStr[1].equalsIgnoreCase("list"))
                {
                    if (par2ArrayOfStr.length > 3)
                    {
                        throw new WrongUsageException("commands.scoreboard.players.list.usage", new Object[0]);
                    }

                    this.func_147195_l(par1ICommandSender, par2ArrayOfStr, 2);
                }
                else if (par2ArrayOfStr[1].equalsIgnoreCase("add"))
                {
                    if (par2ArrayOfStr.length != 5)
                    {
                        throw new WrongUsageException("commands.scoreboard.players.add.usage", new Object[0]);
                    }

                    this.func_147197_m(par1ICommandSender, par2ArrayOfStr, 2);
                }
                else if (par2ArrayOfStr[1].equalsIgnoreCase("remove"))
                {
                    if (par2ArrayOfStr.length != 5)
                    {
                        throw new WrongUsageException("commands.scoreboard.players.remove.usage", new Object[0]);
                    }

                    this.func_147197_m(par1ICommandSender, par2ArrayOfStr, 2);
                }
                else if (par2ArrayOfStr[1].equalsIgnoreCase("set"))
                {
                    if (par2ArrayOfStr.length != 5)
                    {
                        throw new WrongUsageException("commands.scoreboard.players.set.usage", new Object[0]);
                    }

                    this.func_147197_m(par1ICommandSender, par2ArrayOfStr, 2);
                }
                else
                {
                    if (!par2ArrayOfStr[1].equalsIgnoreCase("reset"))
                    {
                        throw new WrongUsageException("commands.scoreboard.players.usage", new Object[0]);
                    }

                    if (par2ArrayOfStr.length != 3)
                    {
                        throw new WrongUsageException("commands.scoreboard.players.reset.usage", new Object[0]);
                    }

                    this.func_147187_n(par1ICommandSender, par2ArrayOfStr, 2);
                }

                return;
            }

            if (par2ArrayOfStr[0].equalsIgnoreCase("teams"))
            {
                if (par2ArrayOfStr.length == 1)
                {
                    throw new WrongUsageException("commands.scoreboard.teams.usage", new Object[0]);
                }

                if (par2ArrayOfStr[1].equalsIgnoreCase("list"))
                {
                    if (par2ArrayOfStr.length > 3)
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.list.usage", new Object[0]);
                    }

                    this.func_147186_g(par1ICommandSender, par2ArrayOfStr, 2);
                }
                else if (par2ArrayOfStr[1].equalsIgnoreCase("add"))
                {
                    if (par2ArrayOfStr.length < 3)
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.add.usage", new Object[0]);
                    }

                    this.func_147185_d(par1ICommandSender, par2ArrayOfStr, 2);
                }
                else if (par2ArrayOfStr[1].equalsIgnoreCase("remove"))
                {
                    if (par2ArrayOfStr.length != 3)
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.remove.usage", new Object[0]);
                    }

                    this.func_147194_f(par1ICommandSender, par2ArrayOfStr, 2);
                }
                else if (par2ArrayOfStr[1].equalsIgnoreCase("empty"))
                {
                    if (par2ArrayOfStr.length != 3)
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.empty.usage", new Object[0]);
                    }

                    this.func_147188_j(par1ICommandSender, par2ArrayOfStr, 2);
                }
                else if (par2ArrayOfStr[1].equalsIgnoreCase("join"))
                {
                    if (par2ArrayOfStr.length < 4 && (par2ArrayOfStr.length != 3 || !(par1ICommandSender instanceof EntityPlayer)))
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.join.usage", new Object[0]);
                    }

                    this.func_147190_h(par1ICommandSender, par2ArrayOfStr, 2);
                }
                else if (par2ArrayOfStr[1].equalsIgnoreCase("leave"))
                {
                    if (par2ArrayOfStr.length < 3 && !(par1ICommandSender instanceof EntityPlayer))
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.leave.usage", new Object[0]);
                    }

                    this.func_147199_i(par1ICommandSender, par2ArrayOfStr, 2);
                }
                else
                {
                    if (!par2ArrayOfStr[1].equalsIgnoreCase("option"))
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.usage", new Object[0]);
                    }

                    if (par2ArrayOfStr.length != 4 && par2ArrayOfStr.length != 5)
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
                    }

                    this.func_147200_e(par1ICommandSender, par2ArrayOfStr, 2);
                }

                return;
            }
        }

        throw new WrongUsageException("commands.scoreboard.usage", new Object[0]);
    }

    protected Scoreboard func_147192_d()
    {
        return MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
    }

    protected ScoreObjective func_147189_a(String p_147189_1_, boolean p_147189_2_)
    {
        Scoreboard scoreboard = this.func_147192_d();
        ScoreObjective scoreobjective = scoreboard.getObjective(p_147189_1_);

        if (scoreobjective == null)
        {
            throw new CommandException("commands.scoreboard.objectiveNotFound", new Object[] {p_147189_1_});
        }
        else if (p_147189_2_ && scoreobjective.getCriteria().isReadOnly())
        {
            throw new CommandException("commands.scoreboard.objectiveReadOnly", new Object[] {p_147189_1_});
        }
        else
        {
            return scoreobjective;
        }
    }

    protected ScorePlayerTeam func_147183_a(String p_147183_1_)
    {
        Scoreboard scoreboard = this.func_147192_d();
        ScorePlayerTeam scoreplayerteam = scoreboard.func_96508_e(p_147183_1_);

        if (scoreplayerteam == null)
        {
            throw new CommandException("commands.scoreboard.teamNotFound", new Object[] {p_147183_1_});
        }
        else
        {
            return scoreplayerteam;
        }
    }

    protected void func_147193_c(ICommandSender p_147193_1_, String[] p_147193_2_, int p_147193_3_)
    {
        String s = p_147193_2_[p_147193_3_++];
        String s1 = p_147193_2_[p_147193_3_++];
        Scoreboard scoreboard = this.func_147192_d();
        IScoreObjectiveCriteria iscoreobjectivecriteria = (IScoreObjectiveCriteria)IScoreObjectiveCriteria.field_96643_a.get(s1);

        if (iscoreobjectivecriteria == null)
        {
            throw new WrongUsageException("commands.scoreboard.objectives.add.wrongType", new Object[] {s1});
        }
        else if (scoreboard.getObjective(s) != null)
        {
            throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", new Object[] {s});
        }
        else if (s.length() > 16)
        {
            throw new SyntaxErrorException("commands.scoreboard.objectives.add.tooLong", new Object[] {s, Integer.valueOf(16)});
        }
        else if (s.length() == 0)
        {
            throw new WrongUsageException("commands.scoreboard.objectives.add.usage", new Object[0]);
        }
        else
        {
            if (p_147193_2_.length > p_147193_3_)
            {
                String s2 = func_147178_a(p_147193_1_, p_147193_2_, p_147193_3_).func_150260_c();

                if (s2.length() > 32)
                {
                    throw new SyntaxErrorException("commands.scoreboard.objectives.add.displayTooLong", new Object[] {s2, Integer.valueOf(32)});
                }

                if (s2.length() > 0)
                {
                    scoreboard.func_96535_a(s, iscoreobjectivecriteria).setDisplayName(s2);
                }
                else
                {
                    scoreboard.func_96535_a(s, iscoreobjectivecriteria);
                }
            }
            else
            {
                scoreboard.func_96535_a(s, iscoreobjectivecriteria);
            }

            notifyAdmins(p_147193_1_, "commands.scoreboard.objectives.add.success", new Object[] {s});
        }
    }

    protected void func_147185_d(ICommandSender p_147185_1_, String[] p_147185_2_, int p_147185_3_)
    {
        String s = p_147185_2_[p_147185_3_++];
        Scoreboard scoreboard = this.func_147192_d();

        if (scoreboard.func_96508_e(s) != null)
        {
            throw new CommandException("commands.scoreboard.teams.add.alreadyExists", new Object[] {s});
        }
        else if (s.length() > 16)
        {
            throw new SyntaxErrorException("commands.scoreboard.teams.add.tooLong", new Object[] {s, Integer.valueOf(16)});
        }
        else if (s.length() == 0)
        {
            throw new WrongUsageException("commands.scoreboard.teams.add.usage", new Object[0]);
        }
        else
        {
            if (p_147185_2_.length > p_147185_3_)
            {
                String s1 = func_147178_a(p_147185_1_, p_147185_2_, p_147185_3_).func_150260_c();

                if (s1.length() > 32)
                {
                    throw new SyntaxErrorException("commands.scoreboard.teams.add.displayTooLong", new Object[] {s1, Integer.valueOf(32)});
                }

                if (s1.length() > 0)
                {
                    scoreboard.createTeam(s).setTeamName(s1);
                }
                else
                {
                    scoreboard.createTeam(s);
                }
            }
            else
            {
                scoreboard.createTeam(s);
            }

            notifyAdmins(p_147185_1_, "commands.scoreboard.teams.add.success", new Object[] {s});
        }
    }

    protected void func_147200_e(ICommandSender p_147200_1_, String[] p_147200_2_, int p_147200_3_)
    {
        ScorePlayerTeam scoreplayerteam = this.func_147183_a(p_147200_2_[p_147200_3_++]);

        if (scoreplayerteam != null)
        {
            String s = p_147200_2_[p_147200_3_++].toLowerCase();

            if (!s.equalsIgnoreCase("color") && !s.equalsIgnoreCase("friendlyfire") && !s.equalsIgnoreCase("seeFriendlyInvisibles"))
            {
                throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
            }
            else if (p_147200_2_.length == 4)
            {
                if (s.equalsIgnoreCase("color"))
                {
                    throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {s, func_96333_a(EnumChatFormatting.func_96296_a(true, false))});
                }
                else if (!s.equalsIgnoreCase("friendlyfire") && !s.equalsIgnoreCase("seeFriendlyInvisibles"))
                {
                    throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
                }
                else
                {
                    throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {s, func_96333_a(Arrays.asList(new String[]{"true", "false"}))});
                }
            }
            else
            {
                String s1 = p_147200_2_[p_147200_3_++];

                if (s.equalsIgnoreCase("color"))
                {
                    EnumChatFormatting enumchatformatting = EnumChatFormatting.func_96300_b(s1);

                    if (enumchatformatting == null || enumchatformatting.func_96301_b())
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {s, func_96333_a(EnumChatFormatting.func_96296_a(true, false))});
                    }

                    scoreplayerteam.setNamePrefix(enumchatformatting.toString());
                    scoreplayerteam.setNameSuffix(EnumChatFormatting.RESET.toString());
                }
                else if (s.equalsIgnoreCase("friendlyfire"))
                {
                    if (!s1.equalsIgnoreCase("true") && !s1.equalsIgnoreCase("false"))
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {s, func_96333_a(Arrays.asList(new String[]{"true", "false"}))});
                    }

                    scoreplayerteam.setAllowFriendlyFire(s1.equalsIgnoreCase("true"));
                }
                else if (s.equalsIgnoreCase("seeFriendlyInvisibles"))
                {
                    if (!s1.equalsIgnoreCase("true") && !s1.equalsIgnoreCase("false"))
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {s, func_96333_a(Arrays.asList(new String[]{"true", "false"}))});
                    }

                    scoreplayerteam.setSeeFriendlyInvisiblesEnabled(s1.equalsIgnoreCase("true"));
                }

                notifyAdmins(p_147200_1_, "commands.scoreboard.teams.option.success", new Object[] {s, scoreplayerteam.func_96661_b(), s1});
            }
        }
    }

    protected void func_147194_f(ICommandSender p_147194_1_, String[] p_147194_2_, int p_147194_3_)
    {
        Scoreboard scoreboard = this.func_147192_d();
        ScorePlayerTeam scoreplayerteam = this.func_147183_a(p_147194_2_[p_147194_3_++]);

        if (scoreplayerteam != null)
        {
            scoreboard.func_96511_d(scoreplayerteam);
            notifyAdmins(p_147194_1_, "commands.scoreboard.teams.remove.success", new Object[] {scoreplayerteam.func_96661_b()});
        }
    }

    protected void func_147186_g(ICommandSender p_147186_1_, String[] p_147186_2_, int p_147186_3_)
    {
        Scoreboard scoreboard = this.func_147192_d();

        if (p_147186_2_.length > p_147186_3_)
        {
            ScorePlayerTeam scoreplayerteam = this.func_147183_a(p_147186_2_[p_147186_3_++]);

            if (scoreplayerteam == null)
            {
                return;
            }

            Collection collection = scoreplayerteam.getMembershipCollection();

            if (collection.size() <= 0)
            {
                throw new CommandException("commands.scoreboard.teams.list.player.empty", new Object[] {scoreplayerteam.func_96661_b()});
            }

            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.scoreboard.teams.list.player.count", new Object[] {Integer.valueOf(collection.size()), scoreplayerteam.func_96661_b()});
            chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
            p_147186_1_.func_145747_a(chatcomponenttranslation);
            p_147186_1_.func_145747_a(new ChatComponentText(joinNiceString(collection.toArray())));
        }
        else
        {
            Collection collection1 = scoreboard.func_96525_g();

            if (collection1.size() <= 0)
            {
                throw new CommandException("commands.scoreboard.teams.list.empty", new Object[0]);
            }

            ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("commands.scoreboard.teams.list.count", new Object[] {Integer.valueOf(collection1.size())});
            chatcomponenttranslation1.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
            p_147186_1_.func_145747_a(chatcomponenttranslation1);
            Iterator iterator = collection1.iterator();

            while (iterator.hasNext())
            {
                ScorePlayerTeam scoreplayerteam1 = (ScorePlayerTeam)iterator.next();
                p_147186_1_.func_145747_a(new ChatComponentTranslation("commands.scoreboard.teams.list.entry", new Object[] {scoreplayerteam1.func_96661_b(), scoreplayerteam1.func_96669_c(), Integer.valueOf(scoreplayerteam1.getMembershipCollection().size())}));
            }
        }
    }

    protected void func_147190_h(ICommandSender p_147190_1_, String[] p_147190_2_, int p_147190_3_)
    {
        Scoreboard scoreboard = this.func_147192_d();
        String s = p_147190_2_[p_147190_3_++];
        HashSet hashset = new HashSet();
        HashSet hashset1 = new HashSet();
        String s1;

        if (p_147190_1_ instanceof EntityPlayer && p_147190_3_ == p_147190_2_.length)
        {
            s1 = getCommandSenderAsPlayer(p_147190_1_).getCommandSenderName();

            if (scoreboard.func_151392_a(s1, s))
            {
                hashset.add(s1);
            }
            else
            {
                hashset1.add(s1);
            }
        }
        else
        {
            while (p_147190_3_ < p_147190_2_.length)
            {
                s1 = func_96332_d(p_147190_1_, p_147190_2_[p_147190_3_++]);

                if (scoreboard.func_151392_a(s1, s))
                {
                    hashset.add(s1);
                }
                else
                {
                    hashset1.add(s1);
                }
            }
        }

        if (!hashset.isEmpty())
        {
            notifyAdmins(p_147190_1_, "commands.scoreboard.teams.join.success", new Object[] {Integer.valueOf(hashset.size()), s, joinNiceString(hashset.toArray(new String[0]))});
        }

        if (!hashset1.isEmpty())
        {
            throw new CommandException("commands.scoreboard.teams.join.failure", new Object[] {Integer.valueOf(hashset1.size()), s, joinNiceString(hashset1.toArray(new String[0]))});
        }
    }

    protected void func_147199_i(ICommandSender p_147199_1_, String[] p_147199_2_, int p_147199_3_)
    {
        Scoreboard scoreboard = this.func_147192_d();
        HashSet hashset = new HashSet();
        HashSet hashset1 = new HashSet();
        String s;

        if (p_147199_1_ instanceof EntityPlayer && p_147199_3_ == p_147199_2_.length)
        {
            s = getCommandSenderAsPlayer(p_147199_1_).getCommandSenderName();

            if (scoreboard.removePlayerFromTeams(s))
            {
                hashset.add(s);
            }
            else
            {
                hashset1.add(s);
            }
        }
        else
        {
            while (p_147199_3_ < p_147199_2_.length)
            {
                s = func_96332_d(p_147199_1_, p_147199_2_[p_147199_3_++]);

                if (scoreboard.removePlayerFromTeams(s))
                {
                    hashset.add(s);
                }
                else
                {
                    hashset1.add(s);
                }
            }
        }

        if (!hashset.isEmpty())
        {
            notifyAdmins(p_147199_1_, "commands.scoreboard.teams.leave.success", new Object[] {Integer.valueOf(hashset.size()), joinNiceString(hashset.toArray(new String[0]))});
        }

        if (!hashset1.isEmpty())
        {
            throw new CommandException("commands.scoreboard.teams.leave.failure", new Object[] {Integer.valueOf(hashset1.size()), joinNiceString(hashset1.toArray(new String[0]))});
        }
    }

    protected void func_147188_j(ICommandSender p_147188_1_, String[] p_147188_2_, int p_147188_3_)
    {
        Scoreboard scoreboard = this.func_147192_d();
        ScorePlayerTeam scoreplayerteam = this.func_147183_a(p_147188_2_[p_147188_3_++]);

        if (scoreplayerteam != null)
        {
            ArrayList arraylist = new ArrayList(scoreplayerteam.getMembershipCollection());

            if (arraylist.isEmpty())
            {
                throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", new Object[] {scoreplayerteam.func_96661_b()});
            }
            else
            {
                Iterator iterator = arraylist.iterator();

                while (iterator.hasNext())
                {
                    String s = (String)iterator.next();
                    scoreboard.removePlayerFromTeam(s, scoreplayerteam);
                }

                notifyAdmins(p_147188_1_, "commands.scoreboard.teams.empty.success", new Object[] {Integer.valueOf(arraylist.size()), scoreplayerteam.func_96661_b()});
            }
        }
    }

    protected void func_147191_h(ICommandSender p_147191_1_, String p_147191_2_)
    {
        Scoreboard scoreboard = this.func_147192_d();
        ScoreObjective scoreobjective = this.func_147189_a(p_147191_2_, false);
        scoreboard.func_96519_k(scoreobjective);
        notifyAdmins(p_147191_1_, "commands.scoreboard.objectives.remove.success", new Object[] {p_147191_2_});
    }

    protected void func_147196_d(ICommandSender p_147196_1_)
    {
        Scoreboard scoreboard = this.func_147192_d();
        Collection collection = scoreboard.getScoreObjectives();

        if (collection.size() <= 0)
        {
            throw new CommandException("commands.scoreboard.objectives.list.empty", new Object[0]);
        }
        else
        {
            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.scoreboard.objectives.list.count", new Object[] {Integer.valueOf(collection.size())});
            chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
            p_147196_1_.func_145747_a(chatcomponenttranslation);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext())
            {
                ScoreObjective scoreobjective = (ScoreObjective)iterator.next();
                p_147196_1_.func_145747_a(new ChatComponentTranslation("commands.scoreboard.objectives.list.entry", new Object[] {scoreobjective.getName(), scoreobjective.getDisplayName(), scoreobjective.getCriteria().func_96636_a()}));
            }
        }
    }

    protected void func_147198_k(ICommandSender p_147198_1_, String[] p_147198_2_, int p_147198_3_)
    {
        Scoreboard scoreboard = this.func_147192_d();
        String s = p_147198_2_[p_147198_3_++];
        int j = Scoreboard.getObjectiveDisplaySlotNumber(s);
        ScoreObjective scoreobjective = null;

        if (p_147198_2_.length == 4)
        {
            scoreobjective = this.func_147189_a(p_147198_2_[p_147198_3_++], false);
        }

        if (j < 0)
        {
            throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", new Object[] {s});
        }
        else
        {
            scoreboard.func_96530_a(j, scoreobjective);

            if (scoreobjective != null)
            {
                notifyAdmins(p_147198_1_, "commands.scoreboard.objectives.setdisplay.successSet", new Object[] {Scoreboard.getObjectiveDisplaySlot(j), scoreobjective.getName()});
            }
            else
            {
                notifyAdmins(p_147198_1_, "commands.scoreboard.objectives.setdisplay.successCleared", new Object[] {Scoreboard.getObjectiveDisplaySlot(j)});
            }
        }
    }

    protected void func_147195_l(ICommandSender p_147195_1_, String[] p_147195_2_, int p_147195_3_)
    {
        Scoreboard scoreboard = this.func_147192_d();

        if (p_147195_2_.length > p_147195_3_)
        {
            String s = func_96332_d(p_147195_1_, p_147195_2_[p_147195_3_++]);
            Map map = scoreboard.func_96510_d(s);

            if (map.size() <= 0)
            {
                throw new CommandException("commands.scoreboard.players.list.player.empty", new Object[] {s});
            }

            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.scoreboard.players.list.player.count", new Object[] {Integer.valueOf(map.size()), s});
            chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
            p_147195_1_.func_145747_a(chatcomponenttranslation);
            Iterator iterator = map.values().iterator();

            while (iterator.hasNext())
            {
                Score score = (Score)iterator.next();
                p_147195_1_.func_145747_a(new ChatComponentTranslation("commands.scoreboard.players.list.player.entry", new Object[] {Integer.valueOf(score.getScorePoints()), score.func_96645_d().getDisplayName(), score.func_96645_d().getName()}));
            }
        }
        else
        {
            Collection collection = scoreboard.getObjectiveNames();

            if (collection.size() <= 0)
            {
                throw new CommandException("commands.scoreboard.players.list.empty", new Object[0]);
            }

            ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("commands.scoreboard.players.list.count", new Object[] {Integer.valueOf(collection.size())});
            chatcomponenttranslation1.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
            p_147195_1_.func_145747_a(chatcomponenttranslation1);
            p_147195_1_.func_145747_a(new ChatComponentText(joinNiceString(collection.toArray())));
        }
    }

    protected void func_147197_m(ICommandSender p_147197_1_, String[] p_147197_2_, int p_147197_3_)
    {
        String s = p_147197_2_[p_147197_3_ - 1];
        String s1 = func_96332_d(p_147197_1_, p_147197_2_[p_147197_3_++]);
        ScoreObjective scoreobjective = this.func_147189_a(p_147197_2_[p_147197_3_++], true);
        int j = s.equalsIgnoreCase("set") ? parseInt(p_147197_1_, p_147197_2_[p_147197_3_++]) : parseIntWithMin(p_147197_1_, p_147197_2_[p_147197_3_++], 1);
        Scoreboard scoreboard = this.func_147192_d();
        Score score = scoreboard.func_96529_a(s1, scoreobjective);

        if (s.equalsIgnoreCase("set"))
        {
            score.func_96647_c(j);
        }
        else if (s.equalsIgnoreCase("add"))
        {
            score.func_96649_a(j);
        }
        else
        {
            score.func_96646_b(j);
        }

        notifyAdmins(p_147197_1_, "commands.scoreboard.players.set.success", new Object[] {scoreobjective.getName(), s1, Integer.valueOf(score.getScorePoints())});
    }

    protected void func_147187_n(ICommandSender p_147187_1_, String[] p_147187_2_, int p_147187_3_)
    {
        Scoreboard scoreboard = this.func_147192_d();
        String s = func_96332_d(p_147187_1_, p_147187_2_[p_147187_3_++]);
        scoreboard.func_96515_c(s);
        notifyAdmins(p_147187_1_, "commands.scoreboard.players.reset.success", new Object[] {s});
    }

    // JAVADOC METHOD $$ func_71516_a
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1)
        {
            // JAVADOC METHOD $$ func_71530_a
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"objectives", "players", "teams"});
        }
        else
        {
            if (par2ArrayOfStr[0].equalsIgnoreCase("objectives"))
            {
                if (par2ArrayOfStr.length == 2)
                {
                    // JAVADOC METHOD $$ func_71530_a
                    return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"list", "add", "remove", "setdisplay"});
                }

                if (par2ArrayOfStr[1].equalsIgnoreCase("add"))
                {
                    if (par2ArrayOfStr.length == 4)
                    {
                        Set set = IScoreObjectiveCriteria.field_96643_a.keySet();
                        // JAVADOC METHOD $$ func_71531_a
                        return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, set);
                    }
                }
                else if (par2ArrayOfStr[1].equalsIgnoreCase("remove"))
                {
                    if (par2ArrayOfStr.length == 3)
                    {
                        // JAVADOC METHOD $$ func_71531_a
                        return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, this.func_147184_a(false));
                    }
                }
                else if (par2ArrayOfStr[1].equalsIgnoreCase("setdisplay"))
                {
                    if (par2ArrayOfStr.length == 3)
                    {
                        // JAVADOC METHOD $$ func_71530_a
                        return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"list", "sidebar", "belowName"});
                    }

                    if (par2ArrayOfStr.length == 4)
                    {
                        // JAVADOC METHOD $$ func_71531_a
                        return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, this.func_147184_a(false));
                    }
                }
            }
            else if (par2ArrayOfStr[0].equalsIgnoreCase("players"))
            {
                if (par2ArrayOfStr.length == 2)
                {
                    // JAVADOC METHOD $$ func_71530_a
                    return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"set", "add", "remove", "reset", "list"});
                }

                if (!par2ArrayOfStr[1].equalsIgnoreCase("set") && !par2ArrayOfStr[1].equalsIgnoreCase("add") && !par2ArrayOfStr[1].equalsIgnoreCase("remove"))
                {
                    if ((par2ArrayOfStr[1].equalsIgnoreCase("reset") || par2ArrayOfStr[1].equalsIgnoreCase("list")) && par2ArrayOfStr.length == 3)
                    {
                        // JAVADOC METHOD $$ func_71531_a
                        return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, this.func_147192_d().getObjectiveNames());
                    }
                }
                else
                {
                    if (par2ArrayOfStr.length == 3)
                    {
                        // JAVADOC METHOD $$ func_71530_a
                        return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
                    }

                    if (par2ArrayOfStr.length == 4)
                    {
                        // JAVADOC METHOD $$ func_71531_a
                        return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, this.func_147184_a(true));
                    }
                }
            }
            else if (par2ArrayOfStr[0].equalsIgnoreCase("teams"))
            {
                if (par2ArrayOfStr.length == 2)
                {
                    // JAVADOC METHOD $$ func_71530_a
                    return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"add", "remove", "join", "leave", "empty", "list", "option"});
                }

                if (par2ArrayOfStr[1].equalsIgnoreCase("join"))
                {
                    if (par2ArrayOfStr.length == 3)
                    {
                        // JAVADOC METHOD $$ func_71531_a
                        return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, this.func_147192_d().func_96531_f());
                    }

                    if (par2ArrayOfStr.length >= 4)
                    {
                        // JAVADOC METHOD $$ func_71530_a
                        return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
                    }
                }
                else
                {
                    if (par2ArrayOfStr[1].equalsIgnoreCase("leave"))
                    {
                        // JAVADOC METHOD $$ func_71530_a
                        return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
                    }

                    if (!par2ArrayOfStr[1].equalsIgnoreCase("empty") && !par2ArrayOfStr[1].equalsIgnoreCase("list") && !par2ArrayOfStr[1].equalsIgnoreCase("remove"))
                    {
                        if (par2ArrayOfStr[1].equalsIgnoreCase("option"))
                        {
                            if (par2ArrayOfStr.length == 3)
                            {
                                // JAVADOC METHOD $$ func_71531_a
                                return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, this.func_147192_d().func_96531_f());
                            }

                            if (par2ArrayOfStr.length == 4)
                            {
                                // JAVADOC METHOD $$ func_71530_a
                                return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"color", "friendlyfire", "seeFriendlyInvisibles"});
                            }

                            if (par2ArrayOfStr.length == 5)
                            {
                                if (par2ArrayOfStr[3].equalsIgnoreCase("color"))
                                {
                                    // JAVADOC METHOD $$ func_71531_a
                                    return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, EnumChatFormatting.func_96296_a(true, false));
                                }

                                if (par2ArrayOfStr[3].equalsIgnoreCase("friendlyfire") || par2ArrayOfStr[3].equalsIgnoreCase("seeFriendlyInvisibles"))
                                {
                                    // JAVADOC METHOD $$ func_71530_a
                                    return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"true", "false"});
                                }
                            }
                        }
                    }
                    else if (par2ArrayOfStr.length == 3)
                    {
                        // JAVADOC METHOD $$ func_71531_a
                        return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, this.func_147192_d().func_96531_f());
                    }
                }
            }

            return null;
        }
    }

    protected List func_147184_a(boolean p_147184_1_)
    {
        Collection collection = this.func_147192_d().getScoreObjectives();
        ArrayList arraylist = new ArrayList();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext())
        {
            ScoreObjective scoreobjective = (ScoreObjective)iterator.next();

            if (!p_147184_1_ || !scoreobjective.getCriteria().isReadOnly())
            {
                arraylist.add(scoreobjective.getName());
            }
        }

        return arraylist;
    }

    // JAVADOC METHOD $$ func_82358_a
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par1ArrayOfStr[0].equalsIgnoreCase("players") ? par2 == 2 : (!par1ArrayOfStr[0].equalsIgnoreCase("teams") ? false : par2 == 2 || par2 == 3);
    }
}