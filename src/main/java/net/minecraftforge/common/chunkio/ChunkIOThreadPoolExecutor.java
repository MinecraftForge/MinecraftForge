package net.minecraftforge.common.chunkio;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.minecraft.crash.CrashReportCategory;
import net.minecraftforge.fml.common.FMLLog;

public class ChunkIOThreadPoolExecutor extends ThreadPoolExecutor {

    public ChunkIOThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t)
    {
        if (t != null)
        {
            try
            {
                FMLLog.log.error("Unhandled exception loading chunk:", t);
                QueuedChunk queuedChunk = ((ChunkIOProvider) r).getChunkInfo();
                FMLLog.log.error(queuedChunk);
                FMLLog.log.error(CrashReportCategory.getCoordinateInfo(queuedChunk.x << 4, 64, queuedChunk.z << 4));
            }
            catch (Throwable t2)
            {
                FMLLog.log.error(t2);
            }
            finally
            {
                // Make absolutely sure that we do not leave any deadlock case
                r.notifyAll();
            }
        }
    }
}
