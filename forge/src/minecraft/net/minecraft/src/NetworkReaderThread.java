package net.minecraft.src;

class NetworkReaderThread extends Thread
{
    /** Reference to the NetworkManager object. */
    final NetworkManager netManager;

    NetworkReaderThread(NetworkManager par1NetworkManager, String par2Str)
    {
        super(par2Str);
        this.netManager = par1NetworkManager;
    }

    public void run()
    {
        Object var1 = NetworkManager.threadSyncObject;

        synchronized (NetworkManager.threadSyncObject)
        {
            ++NetworkManager.numReadThreads;
        }

        while (true)
        {
            boolean var12 = false;

            try
            {
                var12 = true;

                if (!NetworkManager.isRunning(this.netManager))
                {
                    var12 = false;
                    break;
                }

                if (NetworkManager.isServerTerminating(this.netManager))
                {
                    var12 = false;
                    break;
                }

                while (NetworkManager.readNetworkPacket(this.netManager))
                {
                    ;
                }

                try
                {
                    sleep(2L);
                }
                catch (InterruptedException var15)
                {
                    ;
                }
            }
            finally
            {
                if (var12)
                {
                    Object var5 = NetworkManager.threadSyncObject;

                    synchronized (NetworkManager.threadSyncObject)
                    {
                        --NetworkManager.numReadThreads;
                    }
                }
            }
        }

        var1 = NetworkManager.threadSyncObject;

        synchronized (NetworkManager.threadSyncObject)
        {
            --NetworkManager.numReadThreads;
        }
    }
}
