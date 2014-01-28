package net.minecraft.crash;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport
{
    private static final Logger field_147150_a = LogManager.getLogger();
    // JAVADOC FIELD $$ field_71513_a
    private final String description;
    // JAVADOC FIELD $$ field_71511_b
    private final Throwable cause;
    private final CrashReportCategory field_85061_c = new CrashReportCategory(this, "System Details");
    // JAVADOC FIELD $$ field_71512_c
    private final List crashReportSections = new ArrayList();
    // JAVADOC FIELD $$ field_71510_d
    private File crashReportFile;
    private boolean field_85059_f = true;
    private StackTraceElement[] field_85060_g = new StackTraceElement[0];
    private static final String __OBFID = "CL_00000990";

    public CrashReport(String par1Str, Throwable par2Throwable)
    {
        this.description = par1Str;
        this.cause = par2Throwable;
        this.populateEnvironment();
    }

    // JAVADOC METHOD $$ func_71504_g
    private void populateEnvironment()
    {
        this.field_85061_c.addCrashSectionCallable("Minecraft Version", new Callable()
        {
            private static final String __OBFID = "CL_00001197";
            public String call()
            {
                return "1.7.2";
            }
        });
        this.field_85061_c.addCrashSectionCallable("Operating System", new Callable()
        {
            private static final String __OBFID = "CL_00001222";
            public String call()
            {
                return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
            }
        });
        this.field_85061_c.addCrashSectionCallable("Java Version", new Callable()
        {
            private static final String __OBFID = "CL_00001248";
            public String call()
            {
                return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
            }
        });
        this.field_85061_c.addCrashSectionCallable("Java VM Version", new Callable()
        {
            private static final String __OBFID = "CL_00001275";
            public String call()
            {
                return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
            }
        });
        this.field_85061_c.addCrashSectionCallable("Memory", new Callable()
        {
            private static final String __OBFID = "CL_00001302";
            public String call()
            {
                Runtime runtime = Runtime.getRuntime();
                long i = runtime.maxMemory();
                long j = runtime.totalMemory();
                long k = runtime.freeMemory();
                long l = i / 1024L / 1024L;
                long i1 = j / 1024L / 1024L;
                long j1 = k / 1024L / 1024L;
                return k + " bytes (" + j1 + " MB) / " + j + " bytes (" + i1 + " MB) up to " + i + " bytes (" + l + " MB)";
            }
        });
        this.field_85061_c.addCrashSectionCallable("JVM Flags", new Callable()
        {
            private static final String __OBFID = "CL_00001329";
            public String call()
            {
                RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
                List list = runtimemxbean.getInputArguments();
                int i = 0;
                StringBuilder stringbuilder = new StringBuilder();
                Iterator iterator = list.iterator();

                while (iterator.hasNext())
                {
                    String s = (String)iterator.next();

                    if (s.startsWith("-X"))
                    {
                        if (i++ > 0)
                        {
                            stringbuilder.append(" ");
                        }

                        stringbuilder.append(s);
                    }
                }

                return String.format("%d total; %s", new Object[] {Integer.valueOf(i), stringbuilder.toString()});
            }
        });
        this.field_85061_c.addCrashSectionCallable("AABB Pool Size", new Callable()
        {
            private static final String __OBFID = "CL_00001355";
            public String call()
            {
                int i = AxisAlignedBB.getAABBPool().getlistAABBsize();
                int j = 56 * i;
                int k = j / 1024 / 1024;
                int l = AxisAlignedBB.getAABBPool().getnextPoolIndex();
                int i1 = 56 * l;
                int j1 = i1 / 1024 / 1024;
                return i + " (" + j + " bytes; " + k + " MB) allocated, " + l + " (" + i1 + " bytes; " + j1 + " MB) used";
            }
        });
        this.field_85061_c.addCrashSectionCallable("IntCache", new Callable()
        {
            private static final String __OBFID = "CL_00001382";
            public String call() throws SecurityException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException
            {
                return IntCache.func_85144_b();
            }
        });
        FMLCommonHandler.instance().enhanceCrashReport(this, this.field_85061_c);
    }

    // JAVADOC METHOD $$ func_71501_a
    public String getDescription()
    {
        return this.description;
    }

    // JAVADOC METHOD $$ func_71505_b
    public Throwable getCrashCause()
    {
        return this.cause;
    }

    // JAVADOC METHOD $$ func_71506_a
    public void getSectionsInStringBuilder(StringBuilder par1StringBuilder)
    {
        if ((this.field_85060_g == null || this.field_85060_g.length <= 0) && this.crashReportSections.size() > 0)
        {
            this.field_85060_g = (StackTraceElement[])ArrayUtils.subarray(((CrashReportCategory)this.crashReportSections.get(0)).func_147152_a(), 0, 1);
        }

        if (this.field_85060_g != null && this.field_85060_g.length > 0)
        {
            par1StringBuilder.append("-- Head --\n");
            par1StringBuilder.append("Stacktrace:\n");
            StackTraceElement[] astacktraceelement = this.field_85060_g;
            int i = astacktraceelement.length;

            for (int j = 0; j < i; ++j)
            {
                StackTraceElement stacktraceelement = astacktraceelement[j];
                par1StringBuilder.append("\t").append("at ").append(stacktraceelement.toString());
                par1StringBuilder.append("\n");
            }

            par1StringBuilder.append("\n");
        }

        Iterator iterator = this.crashReportSections.iterator();

        while (iterator.hasNext())
        {
            CrashReportCategory crashreportcategory = (CrashReportCategory)iterator.next();
            crashreportcategory.func_85072_a(par1StringBuilder);
            par1StringBuilder.append("\n\n");
        }

        this.field_85061_c.func_85072_a(par1StringBuilder);
    }

    // JAVADOC METHOD $$ func_71498_d
    public String getCauseStackTraceOrString()
    {
        StringWriter stringwriter = null;
        PrintWriter printwriter = null;
        Object object = this.cause;

        if (((Throwable)object).getMessage() == null)
        {
            if (object instanceof NullPointerException)
            {
                object = new NullPointerException(this.description);
            }
            else if (object instanceof StackOverflowError)
            {
                object = new StackOverflowError(this.description);
            }
            else if (object instanceof OutOfMemoryError)
            {
                object = new OutOfMemoryError(this.description);
            }

            ((Throwable)object).setStackTrace(this.cause.getStackTrace());
        }

        String s = ((Throwable)object).toString();

        try
        {
            stringwriter = new StringWriter();
            printwriter = new PrintWriter(stringwriter);
            ((Throwable)object).printStackTrace(printwriter);
            s = stringwriter.toString();
        }
        finally
        {
            IOUtils.closeQuietly(stringwriter);
            IOUtils.closeQuietly(printwriter);
        }

        return s;
    }

    // JAVADOC METHOD $$ func_71502_e
    public String getCompleteReport()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("---- Minecraft Crash Report ----\n");
        stringbuilder.append("// ");
        stringbuilder.append(getWittyComment());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time: ");
        stringbuilder.append((new SimpleDateFormat()).format(new Date()));
        stringbuilder.append("\n");
        stringbuilder.append("Description: ");
        stringbuilder.append(this.description);
        stringbuilder.append("\n\n");
        stringbuilder.append(this.getCauseStackTraceOrString());
        stringbuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

        for (int i = 0; i < 87; ++i)
        {
            stringbuilder.append("-");
        }

        stringbuilder.append("\n\n");
        this.getSectionsInStringBuilder(stringbuilder);
        return stringbuilder.toString();
    }

    // JAVADOC METHOD $$ func_71497_f
    @SideOnly(Side.CLIENT)
    public File getFile()
    {
        return this.crashReportFile;
    }

    public boolean func_147149_a(File p_147149_1_)
    {
        if (this.crashReportFile != null)
        {
            return false;
        }
        else
        {
            if (p_147149_1_.getParentFile() != null)
            {
                p_147149_1_.getParentFile().mkdirs();
            }

            try
            {
                FileWriter filewriter = new FileWriter(p_147149_1_);
                filewriter.write(this.getCompleteReport());
                filewriter.close();
                this.crashReportFile = p_147149_1_;
                return true;
            }
            catch (Throwable throwable)
            {
                field_147150_a.error("Could not save crash report to " + p_147149_1_, throwable);
                return false;
            }
        }
    }

    public CrashReportCategory getCategory()
    {
        return this.field_85061_c;
    }

    // JAVADOC METHOD $$ func_85058_a
    public CrashReportCategory makeCategory(String par1Str)
    {
        return this.makeCategoryDepth(par1Str, 1);
    }

    // JAVADOC METHOD $$ func_85057_a
    public CrashReportCategory makeCategoryDepth(String par1Str, int par2)
    {
        CrashReportCategory crashreportcategory = new CrashReportCategory(this, par1Str);

        if (this.field_85059_f)
        {
            int j = crashreportcategory.func_85073_a(par2);
            StackTraceElement[] astacktraceelement = this.cause.getStackTrace();
            StackTraceElement stacktraceelement = null;
            StackTraceElement stacktraceelement1 = null;

            int idx = astacktraceelement.length - j; //Forge fix AIOOB exception.
            if (astacktraceelement != null && idx < astacktraceelement.length && idx >= 0)
            {
                stacktraceelement = astacktraceelement[astacktraceelement.length - j];

                if (astacktraceelement.length + 1 - j < astacktraceelement.length)
                {
                    stacktraceelement1 = astacktraceelement[astacktraceelement.length + 1 - j];
                }
            }

            this.field_85059_f = crashreportcategory.func_85069_a(stacktraceelement, stacktraceelement1);

            if (j > 0 && !this.crashReportSections.isEmpty())
            {
                CrashReportCategory crashreportcategory1 = (CrashReportCategory)this.crashReportSections.get(this.crashReportSections.size() - 1);
                crashreportcategory1.func_85070_b(j);
            }
            else if (astacktraceelement != null && astacktraceelement.length >= j)
            {
                this.field_85060_g = new StackTraceElement[astacktraceelement.length - j];
                System.arraycopy(astacktraceelement, 0, this.field_85060_g, 0, this.field_85060_g.length);
            }
            else
            {
                this.field_85059_f = false;
            }
        }

        this.crashReportSections.add(crashreportcategory);
        return crashreportcategory;
    }

    // JAVADOC METHOD $$ func_71503_h
    private static String getWittyComment()
    {
        String[] astring = new String[] {"Who set us up the TNT?", "Everything\'s going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I\'m sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don\'t be sad. I\'ll do better next time, I promise!", "Don\'t be sad, have a hug! <3", "I just don\'t know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn\'t worry myself about that.", "I bet Cylons wouldn\'t have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I\'m Minecraft, and I\'m a crashaholic.", "Ooh. Shiny.", "This doesn\'t make any sense!", "Why is it breaking :(", "Don\'t do that.", "Ouch. That hurt :(", "You\'re mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!"};

        try
        {
            return astring[(int)(System.nanoTime() % (long)astring.length)];
        }
        catch (Throwable throwable)
        {
            return "Witty comment unavailable :(";
        }
    }

    // JAVADOC METHOD $$ func_85055_a
    public static CrashReport makeCrashReport(Throwable par0Throwable, String par1Str)
    {
        CrashReport crashreport;

        if (par0Throwable instanceof ReportedException)
        {
            crashreport = ((ReportedException)par0Throwable).getCrashReport();
        }
        else
        {
            crashreport = new CrashReport(par1Str, par0Throwable);
        }

        return crashreport;
    }
}