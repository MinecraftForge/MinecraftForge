package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSpreadPlayers extends CommandBase
{
    private static final String __OBFID = "CL_00001080";

    public String getCommandName()
    {
        return "spreadplayers";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.spreadplayers.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length < 6)
        {
            throw new WrongUsageException("commands.spreadplayers.usage", new Object[0]);
        }
        else
        {
            byte b0 = 0;
            int i = b0 + 1;
            double d0 = func_110666_a(par1ICommandSender, Double.NaN, par2ArrayOfStr[b0]);
            double d1 = func_110666_a(par1ICommandSender, Double.NaN, par2ArrayOfStr[i++]);
            double d2 = func_110664_a(par1ICommandSender, par2ArrayOfStr[i++], 0.0D);
            double d3 = func_110664_a(par1ICommandSender, par2ArrayOfStr[i++], d2 + 1.0D);
            boolean flag = func_110662_c(par1ICommandSender, par2ArrayOfStr[i++]);
            ArrayList arraylist = Lists.newArrayList();

            while (true)
            {
                while (i < par2ArrayOfStr.length)
                {
                    String s = par2ArrayOfStr[i++];

                    if (PlayerSelector.hasArguments(s))
                    {
                        EntityPlayerMP[] aentityplayermp = PlayerSelector.matchPlayers(par1ICommandSender, s);

                        if (aentityplayermp == null || aentityplayermp.length == 0)
                        {
                            throw new PlayerNotFoundException();
                        }

                        Collections.addAll(arraylist, aentityplayermp);
                    }
                    else
                    {
                        EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(s);

                        if (entityplayermp == null)
                        {
                            throw new PlayerNotFoundException();
                        }

                        arraylist.add(entityplayermp);
                    }
                }

                if (arraylist.isEmpty())
                {
                    throw new PlayerNotFoundException();
                }

                par1ICommandSender.func_145747_a(new ChatComponentTranslation("commands.spreadplayers.spreading." + (flag ? "teams" : "players"), new Object[] {Integer.valueOf(arraylist.size()), Double.valueOf(d3), Double.valueOf(d0), Double.valueOf(d1), Double.valueOf(d2)}));
                this.func_110669_a(par1ICommandSender, arraylist, new CommandSpreadPlayers.Position(d0, d1), d2, d3, ((EntityLivingBase)arraylist.get(0)).worldObj, flag);
                return;
            }
        }
    }

    private void func_110669_a(ICommandSender par1ICommandSender, List par2List, CommandSpreadPlayers.Position par3CommandSpreadPlayersPosition, double par4, double par6, World par8World, boolean par9)
    {
        Random random = new Random();
        double d2 = par3CommandSpreadPlayersPosition.field_111101_a - par6;
        double d3 = par3CommandSpreadPlayersPosition.field_111100_b - par6;
        double d4 = par3CommandSpreadPlayersPosition.field_111101_a + par6;
        double d5 = par3CommandSpreadPlayersPosition.field_111100_b + par6;
        CommandSpreadPlayers.Position[] aposition = this.func_110670_a(random, par9 ? this.func_110667_a(par2List) : par2List.size(), d2, d3, d4, d5);
        int i = this.func_110668_a(par3CommandSpreadPlayersPosition, par4, par8World, random, d2, d3, d4, d5, aposition, par9);
        double d6 = this.func_110671_a(par2List, par8World, aposition, par9);
        notifyAdmins(par1ICommandSender, "commands.spreadplayers.success." + (par9 ? "teams" : "players"), new Object[] {Integer.valueOf(aposition.length), Double.valueOf(par3CommandSpreadPlayersPosition.field_111101_a), Double.valueOf(par3CommandSpreadPlayersPosition.field_111100_b)});

        if (aposition.length > 1)
        {
            par1ICommandSender.func_145747_a(new ChatComponentTranslation("commands.spreadplayers.info." + (par9 ? "teams" : "players"), new Object[] {String.format("%.2f", new Object[]{Double.valueOf(d6)}), Integer.valueOf(i)}));
        }
    }

    private int func_110667_a(List par1List)
    {
        HashSet hashset = Sets.newHashSet();
        Iterator iterator = par1List.iterator();

        while (iterator.hasNext())
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)iterator.next();

            if (entitylivingbase instanceof EntityPlayer)
            {
                hashset.add(entitylivingbase.getTeam());
            }
            else
            {
                hashset.add((Object)null);
            }
        }

        return hashset.size();
    }

    private int func_110668_a(CommandSpreadPlayers.Position par1CommandSpreadPlayersPosition, double par2, World par4World, Random par5Random, double par6, double par8, double par10, double par12, CommandSpreadPlayers.Position[] par14ArrayOfCommandSpreadPlayersPosition, boolean par15)
    {
        boolean flag1 = true;
        double d5 = 3.4028234663852886E38D;
        int i;

        for (i = 0; i < 10000 && flag1; ++i)
        {
            flag1 = false;
            d5 = 3.4028234663852886E38D;
            CommandSpreadPlayers.Position position1;
            int k;

            for (int j = 0; j < par14ArrayOfCommandSpreadPlayersPosition.length; ++j)
            {
                CommandSpreadPlayers.Position position = par14ArrayOfCommandSpreadPlayersPosition[j];
                k = 0;
                position1 = new CommandSpreadPlayers.Position();

                for (int l = 0; l < par14ArrayOfCommandSpreadPlayersPosition.length; ++l)
                {
                    if (j != l)
                    {
                        CommandSpreadPlayers.Position position2 = par14ArrayOfCommandSpreadPlayersPosition[l];
                        double d6 = position.func_111099_a(position2);
                        d5 = Math.min(d6, d5);

                        if (d6 < par2)
                        {
                            ++k;
                            position1.field_111101_a += position2.field_111101_a - position.field_111101_a;
                            position1.field_111100_b += position2.field_111100_b - position.field_111100_b;
                        }
                    }
                }

                if (k > 0)
                {
                    position1.field_111101_a /= (double)k;
                    position1.field_111100_b /= (double)k;
                    double d7 = (double)position1.func_111096_b();

                    if (d7 > 0.0D)
                    {
                        position1.func_111095_a();
                        position.func_111094_b(position1);
                    }
                    else
                    {
                        position.func_111097_a(par5Random, par6, par8, par10, par12);
                    }

                    flag1 = true;
                }

                if (position.func_111093_a(par6, par8, par10, par12))
                {
                    flag1 = true;
                }
            }

            if (!flag1)
            {
                CommandSpreadPlayers.Position[] aposition = par14ArrayOfCommandSpreadPlayersPosition;
                int i1 = par14ArrayOfCommandSpreadPlayersPosition.length;

                for (k = 0; k < i1; ++k)
                {
                    position1 = aposition[k];

                    if (!position1.func_111098_b(par4World))
                    {
                        position1.func_111097_a(par5Random, par6, par8, par10, par12);
                        flag1 = true;
                    }
                }
            }
        }

        if (i >= 10000)
        {
            throw new CommandException("commands.spreadplayers.failure." + (par15 ? "teams" : "players"), new Object[] {Integer.valueOf(par14ArrayOfCommandSpreadPlayersPosition.length), Double.valueOf(par1CommandSpreadPlayersPosition.field_111101_a), Double.valueOf(par1CommandSpreadPlayersPosition.field_111100_b), String.format("%.2f", new Object[]{Double.valueOf(d5)})});
        }
        else
        {
            return i;
        }
    }

    private double func_110671_a(List par1List, World par2World, CommandSpreadPlayers.Position[] par3ArrayOfCommandSpreadPlayersPosition, boolean par4)
    {
        double d0 = 0.0D;
        int i = 0;
        HashMap hashmap = Maps.newHashMap();

        for (int j = 0; j < par1List.size(); ++j)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)par1List.get(j);
            CommandSpreadPlayers.Position position;

            if (par4)
            {
                Team team = entitylivingbase instanceof EntityPlayer ? entitylivingbase.getTeam() : null;

                if (!hashmap.containsKey(team))
                {
                    hashmap.put(team, par3ArrayOfCommandSpreadPlayersPosition[i++]);
                }

                position = (CommandSpreadPlayers.Position)hashmap.get(team);
            }
            else
            {
                position = par3ArrayOfCommandSpreadPlayersPosition[i++];
            }

            entitylivingbase.setPositionAndUpdate((double)((float)MathHelper.floor_double(position.field_111101_a) + 0.5F), (double)position.func_111092_a(par2World), (double)MathHelper.floor_double(position.field_111100_b) + 0.5D);
            double d2 = Double.MAX_VALUE;

            for (int k = 0; k < par3ArrayOfCommandSpreadPlayersPosition.length; ++k)
            {
                if (position != par3ArrayOfCommandSpreadPlayersPosition[k])
                {
                    double d1 = position.func_111099_a(par3ArrayOfCommandSpreadPlayersPosition[k]);
                    d2 = Math.min(d1, d2);
                }
            }

            d0 += d2;
        }

        d0 /= (double)par1List.size();
        return d0;
    }

    private CommandSpreadPlayers.Position[] func_110670_a(Random par1Random, int par2, double par3, double par5, double par7, double par9)
    {
        CommandSpreadPlayers.Position[] aposition = new CommandSpreadPlayers.Position[par2];

        for (int j = 0; j < aposition.length; ++j)
        {
            CommandSpreadPlayers.Position position = new CommandSpreadPlayers.Position();
            position.func_111097_a(par1Random, par3, par5, par7, par9);
            aposition[j] = position;
        }

        return aposition;
    }

    static class Position
        {
            double field_111101_a;
            double field_111100_b;
            private static final String __OBFID = "CL_00001105";

            Position() {}

            Position(double par1, double par3)
            {
                this.field_111101_a = par1;
                this.field_111100_b = par3;
            }

            double func_111099_a(CommandSpreadPlayers.Position par1CommandSpreadPlayersPosition)
            {
                double d0 = this.field_111101_a - par1CommandSpreadPlayersPosition.field_111101_a;
                double d1 = this.field_111100_b - par1CommandSpreadPlayersPosition.field_111100_b;
                return Math.sqrt(d0 * d0 + d1 * d1);
            }

            void func_111095_a()
            {
                double d0 = (double)this.func_111096_b();
                this.field_111101_a /= d0;
                this.field_111100_b /= d0;
            }

            float func_111096_b()
            {
                return MathHelper.sqrt_double(this.field_111101_a * this.field_111101_a + this.field_111100_b * this.field_111100_b);
            }

            public void func_111094_b(CommandSpreadPlayers.Position par1CommandSpreadPlayersPosition)
            {
                this.field_111101_a -= par1CommandSpreadPlayersPosition.field_111101_a;
                this.field_111100_b -= par1CommandSpreadPlayersPosition.field_111100_b;
            }

            public boolean func_111093_a(double par1, double par3, double par5, double par7)
            {
                boolean flag = false;

                if (this.field_111101_a < par1)
                {
                    this.field_111101_a = par1;
                    flag = true;
                }
                else if (this.field_111101_a > par5)
                {
                    this.field_111101_a = par5;
                    flag = true;
                }

                if (this.field_111100_b < par3)
                {
                    this.field_111100_b = par3;
                    flag = true;
                }
                else if (this.field_111100_b > par7)
                {
                    this.field_111100_b = par7;
                    flag = true;
                }

                return flag;
            }

            public int func_111092_a(World par1World)
            {
                int i = MathHelper.floor_double(this.field_111101_a);
                int j = MathHelper.floor_double(this.field_111100_b);

                for (int k = 256; k > 0; --k)
                {
                    if (par1World.func_147439_a(i, k, j).func_149688_o() != Material.field_151579_a)
                    {
                        return k + 1;
                    }
                }

                return 257;
            }

            public boolean func_111098_b(World par1World)
            {
                int i = MathHelper.floor_double(this.field_111101_a);
                int j = MathHelper.floor_double(this.field_111100_b);
                short short1 = 256;

                if (short1 <= 0)
                {
                    return false;
                }
                else
                {
                    Material material = par1World.func_147439_a(i, short1, j).func_149688_o();
                    return !material.isLiquid() && material != Material.field_151581_o;
                }
            }

            public void func_111097_a(Random par1Random, double par2, double par4, double par6, double par8)
            {
                this.field_111101_a = MathHelper.getRandomDoubleInRange(par1Random, par2, par6);
                this.field_111100_b = MathHelper.getRandomDoubleInRange(par1Random, par4, par8);
            }
        }
}