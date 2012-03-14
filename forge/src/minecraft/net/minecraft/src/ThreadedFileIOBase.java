package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadedFileIOBase implements Runnable
{
    /** Instance of ThreadedFileIOBase */
    public static final ThreadedFileIOBase threadedIOInstance = new ThreadedFileIOBase();
    private List threadedIOQueue = Collections.synchronizedList(new ArrayList());
    private volatile long writeQueuedCounter = 0L;
    private volatile long savedIOCounter = 0L;
    private volatile boolean isThreadWaiting = false;

    private ThreadedFileIOBase()
    {
        Thread var1 = new Thread(this, "File IO Thread");
        var1.setPriority(1);
        var1.start();
    }

    public void run()
    {
        while (true)
        {
            this.processQueue();
        }
    }

    /**
     * Process the items that are in the queue
     */
    private void processQueue()
    {
        for (int var1 = 0; var1 < this.threadedIOQueue.size(); ++var1)
        {
            IThreadedFileIO var2 = (IThreadedFileIO)this.threadedIOQueue.get(var1);
            boolean var3 = var2.writeNextIO();

            if (!var3)
            {
                this.threadedIOQueue.remove(var1--);
                ++this.savedIOCounter;
            }

            try
            {
                if (!this.isThreadWaiting)
                {
                    Thread.sleep(10L);
                }
                else
                {
                    Thread.sleep(0L);
                }
            }
            catch (InterruptedException var6)
            {
                var6.printStackTrace();
            }
        }

        if (this.threadedIOQueue.isEmpty())
        {
            try
            {
                Thread.sleep(25L);
            }
            catch (InterruptedException var5)
            {
                var5.printStackTrace();
            }
        }
    }

    /**
     * threaded io
     */
    public void queueIO(IThreadedFileIO par1IThreadedFileIO)
    {
        if (!this.threadedIOQueue.contains(par1IThreadedFileIO))
        {
            ++this.writeQueuedCounter;
            this.threadedIOQueue.add(par1IThreadedFileIO);
        }
    }

    public void waitForFinish() throws InterruptedException
    {
        this.isThreadWaiting = true;

        while (this.writeQueuedCounter != this.savedIOCounter)
        {
            Thread.sleep(10L);
        }

        this.isThreadWaiting = false;
    }
}
