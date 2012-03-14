package net.minecraft.src;

import java.util.Map;

class ThreadStatSyncherSend extends Thread
{
    final Map field_27233_a;

    final StatsSyncher syncher;

    ThreadStatSyncherSend(StatsSyncher par1StatsSyncher, Map par2Map)
    {
        this.syncher = par1StatsSyncher;
        this.field_27233_a = par2Map;
    }

    public void run()
    {
        try
        {
            StatsSyncher.func_27412_a(this.syncher, this.field_27233_a, StatsSyncher.getUnsentDataFile(this.syncher), StatsSyncher.getUnsentTempFile(this.syncher), StatsSyncher.getUnsentOldFile(this.syncher));
        }
        catch (Exception var5)
        {
            var5.printStackTrace();
        }
        finally
        {
            StatsSyncher.setBusy(this.syncher, false);
        }
    }
}
