package net.minecraft.src;

class ThreadMonitorConnection extends Thread
{
    /** Reference to the NetworkManager object. */
    final NetworkManager netManager;

    ThreadMonitorConnection(NetworkManager par1NetworkManager)
    {
        this.netManager = par1NetworkManager;
    }

    public void run()
    {
        try
        {
            Thread.sleep(2000L);

            if (NetworkManager.isRunning(this.netManager))
            {
                NetworkManager.getWriteThread(this.netManager).interrupt();
                this.netManager.networkShutdown("disconnect.closed", new Object[0]);
            }
        }
        catch (Exception var2)
        {
            var2.printStackTrace();
        }
    }
}
