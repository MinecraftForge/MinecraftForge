/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.server.command.world;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentBase;
import net.minecraftforge.server.command.TextComponentHelper;

import java.util.concurrent.Executors;

abstract class AbstractWorldJob extends Thread
{
    private static final int MS_SECOND = 1000;
    private static final int MS_MINUTE = 60 * MS_SECOND;
    private static final int MS_HOUR = 60 * MS_MINUTE;

    private static final String SWITCH_NOW_KEY = "commands.forge.loadworld.load.switch_now";
    private static final String DELAY_KEY = "commands.forge.loadworld.load.delay";

    protected MinecraftServer server;
    protected ICommandSender sender;
    String folderName;
    private int delayMs;

    private boolean cancelled = false;

    AbstractWorldJob(MinecraftServer server, ICommandSender sender, String folderName, int delayS)
    {
        this.server = server;
        this.sender = sender;
        this.folderName = folderName;
        this.delayMs = delayS * 1000;

        sendDelayMsg(sender, delayMs);

        setDaemon(true);
    }

    public abstract void executeWorldJob();

    @Override
    public void run()
    {
        waitDelay();
        if (cancelled)
        {
            return;
        }
        kickPlayers();
        if (cancelled)
        {
            return;
        }
        callFromMainThread(server, this::executeWorldJob);
    }

    private void waitDelay()
    {
        int[] mentionTimes = {AbstractWorldJob.MS_HOUR, AbstractWorldJob.MS_MINUTE * 10, AbstractWorldJob.MS_MINUTE, AbstractWorldJob.MS_SECOND * 30, AbstractWorldJob.MS_SECOND * 10};
        try
        {
            for (int time : mentionTimes)
            {
                if (delayMs > time)
                {
                    int sleep = delayMs - time;
                    delayMs -= sleep;
                    Thread.sleep(sleep);
                    if (cancelled)
                    {
                        return;
                    }
                    sendDelayMsgToAllPlayers(server, delayMs);
                }
            }
            Thread.sleep(delayMs);
            return;
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if (delayMs > 0)
        { // wait did not succeed
            waitDelay();
        }
    }

    private void kickPlayers()
    {
        while (server.getCurrentPlayerCount() > 0)
        {
            sendDelayMsgToAllPlayers(server, 0);
            callFromMainThread(server, () ->
            {
                for (EntityPlayerMP p : server.getPlayerList().getPlayers())
                {
                    p.connection.disconnect(TextComponentHelper.createComponentTranslation(server, "commands.forge.loadworld.load.kick_msg"));
                }
            });
            for (int i = 0; i < 5 && server.getCurrentPlayerCount() > 0; i++)
            {
                try
                {
                    Thread.sleep(100 * i);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendDelayMsg(ICommandSender sender, int delayMs)
    {
        sender.sendMessage(getTextComponent(delayMs));
    }

    private void sendDelayMsgToAllPlayers(MinecraftServer server, int delayMs)
    {
        server.getPlayerList().sendMessage(getTextComponent(delayMs));
    }

    private TextComponentBase getTextComponent(int delayMs)
    {
        return TextComponentHelper.createComponentTranslation(server, delayMs <= 0 ? AbstractWorldJob.SWITCH_NOW_KEY : AbstractWorldJob.DELAY_KEY, delayMs / 1000);
    }

    private void callFromMainThread(MinecraftServer server, Runnable run)
    {
        server.callFromMainThread(Executors.callable(run));
    }

    public void cancel()
    {
        cancelled = true;
    }

    public boolean canceled()
    {
        return cancelled;
    }
}
