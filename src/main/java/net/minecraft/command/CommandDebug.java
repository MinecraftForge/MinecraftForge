package net.minecraft.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug extends CommandBase
{
    private static final Logger field_147208_a = LogManager.getLogger();
    private long field_147206_b;
    private int field_147207_c;
    private static final String __OBFID = "CL_00000270";

    public String getCommandName()
    {
        return "debug";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.debug.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1)
        {
            if (par2ArrayOfStr[0].equals("start"))
            {
                notifyAdmins(par1ICommandSender, "commands.debug.start", new Object[0]);
                MinecraftServer.getServer().enableProfiling();
                this.field_147206_b = MinecraftServer.getSystemTimeMillis();
                this.field_147207_c = MinecraftServer.getServer().getTickCounter();
                return;
            }

            if (par2ArrayOfStr[0].equals("stop"))
            {
                if (!MinecraftServer.getServer().theProfiler.profilingEnabled)
                {
                    throw new CommandException("commands.debug.notStarted", new Object[0]);
                }

                long i = MinecraftServer.getSystemTimeMillis();
                int j = MinecraftServer.getServer().getTickCounter();
                long k = i - this.field_147206_b;
                int l = j - this.field_147207_c;
                this.func_147205_a(k, l);
                MinecraftServer.getServer().theProfiler.profilingEnabled = false;
                notifyAdmins(par1ICommandSender, "commands.debug.stop", new Object[] {Float.valueOf((float)k / 1000.0F), Integer.valueOf(l)});
                return;
            }
        }

        throw new WrongUsageException("commands.debug.usage", new Object[0]);
    }

    private void func_147205_a(long p_147205_1_, int p_147205_3_)
    {
        File file1 = new File(MinecraftServer.getServer().getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
        file1.getParentFile().mkdirs();

        try
        {
            FileWriter filewriter = new FileWriter(file1);
            filewriter.write(this.func_147204_b(p_147205_1_, p_147205_3_));
            filewriter.close();
        }
        catch (Throwable throwable)
        {
            field_147208_a.error("Could not save profiler results to " + file1, throwable);
        }
    }

    private String func_147204_b(long p_147204_1_, int p_147204_3_)
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("---- Minecraft Profiler Results ----\n");
        stringbuilder.append("// ");
        stringbuilder.append(func_147203_d());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time span: ").append(p_147204_1_).append(" ms\n");
        stringbuilder.append("Tick span: ").append(p_147204_3_).append(" ticks\n");
        stringbuilder.append("// This is approximately ").append(String.format("%.2f", new Object[] {Float.valueOf((float)p_147204_3_ / ((float)p_147204_1_ / 1000.0F))})).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.func_147202_a(0, "root", stringbuilder);
        stringbuilder.append("--- END PROFILE DUMP ---\n\n");
        return stringbuilder.toString();
    }

    private void func_147202_a(int p_147202_1_, String p_147202_2_, StringBuilder p_147202_3_)
    {
        List list = MinecraftServer.getServer().theProfiler.getProfilingData(p_147202_2_);

        if (list != null && list.size() >= 3)
        {
            for (int j = 1; j < list.size(); ++j)
            {
                Profiler.Result result = (Profiler.Result)list.get(j);
                p_147202_3_.append(String.format("[%02d] ", new Object[] {Integer.valueOf(p_147202_1_)}));

                for (int k = 0; k < p_147202_1_; ++k)
                {
                    p_147202_3_.append(" ");
                }

                p_147202_3_.append(result.field_76331_c);
                p_147202_3_.append(" - ");
                p_147202_3_.append(String.format("%.2f", new Object[] {Double.valueOf(result.field_76332_a)}));
                p_147202_3_.append("%/");
                p_147202_3_.append(String.format("%.2f", new Object[] {Double.valueOf(result.field_76330_b)}));
                p_147202_3_.append("%\n");

                if (!result.field_76331_c.equals("unspecified"))
                {
                    try
                    {
                        this.func_147202_a(p_147202_1_ + 1, p_147202_2_ + "." + result.field_76331_c, p_147202_3_);
                    }
                    catch (Exception exception)
                    {
                        p_147202_3_.append("[[ EXCEPTION " + exception + " ]]");
                    }
                }
            }
        }
    }

    private static String func_147203_d()
    {
        String[] astring = new String[] {"Shiny numbers!", "Am I not running fast enough? :(", "I\'m working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it\'ll have more motivation to work faster! Poor server."};

        try
        {
            return astring[(int)(System.nanoTime() % (long)astring.length)];
        }
        catch (Throwable throwable)
        {
            return "Witty comment unavailable :(";
        }
    }

    // JAVADOC METHOD $$ func_71516_a
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"start", "stop"}): null;
    }
}