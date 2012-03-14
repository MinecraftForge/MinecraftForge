package net.minecraft.src;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class GuiLogFormatter extends Formatter
{
    /** Reference to the GuiLogOutputHandler. */
    final GuiLogOutputHandler outputHandler;

    GuiLogFormatter(GuiLogOutputHandler par1GuiLogOutputHandler)
    {
        this.outputHandler = par1GuiLogOutputHandler;
    }

    public String format(LogRecord par1LogRecord)
    {
        StringBuilder var2 = new StringBuilder();
        Level var3 = par1LogRecord.getLevel();

        if (var3 == Level.FINEST)
        {
            var2.append("[FINEST] ");
        }
        else if (var3 == Level.FINER)
        {
            var2.append("[FINER] ");
        }
        else if (var3 == Level.FINE)
        {
            var2.append("[FINE] ");
        }
        else if (var3 == Level.INFO)
        {
            var2.append("[INFO] ");
        }
        else if (var3 == Level.WARNING)
        {
            var2.append("[WARNING] ");
        }
        else if (var3 == Level.SEVERE)
        {
            var2.append("[SEVERE] ");
        }
        else if (var3 == Level.SEVERE)
        {
            var2.append("[" + var3.getLocalizedName() + "] ");
        }

        var2.append(par1LogRecord.getMessage());
        var2.append('\n');
        Throwable var4 = par1LogRecord.getThrown();

        if (var4 != null)
        {
            StringWriter var5 = new StringWriter();
            var4.printStackTrace(new PrintWriter(var5));
            var2.append(var5.toString());
        }

        return var2.toString();
    }
}
