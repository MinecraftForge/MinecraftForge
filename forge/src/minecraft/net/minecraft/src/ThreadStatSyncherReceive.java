package net.minecraft.src;

class ThreadStatSyncherReceive extends Thread
{
    final StatsSyncher syncher;

    ThreadStatSyncherReceive(StatsSyncher par1StatsSyncher)
    {
        this.syncher = par1StatsSyncher;
    }

    public void run()
    {
        try
        {
            if (StatsSyncher.func_27422_a(this.syncher) != null)
            {
                StatsSyncher.func_27412_a(this.syncher, StatsSyncher.func_27422_a(this.syncher), StatsSyncher.func_27423_b(this.syncher), StatsSyncher.func_27411_c(this.syncher), StatsSyncher.func_27413_d(this.syncher));
            }
            else if (StatsSyncher.func_27423_b(this.syncher).exists())
            {
                StatsSyncher.func_27421_a(this.syncher, StatsSyncher.func_27409_a(this.syncher, StatsSyncher.func_27423_b(this.syncher), StatsSyncher.func_27411_c(this.syncher), StatsSyncher.func_27413_d(this.syncher)));
            }
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
