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
package net.minecraftforge.server.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.WorldNotFoundException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

class CommandLoadWorld extends CommandBase
{
    private static final int MS_SECOND = 1000;
    private static final int MS_MINUTE = 60 * MS_SECOND;
    private static final int MS_HOUR = 60 * MS_MINUTE;

    private static final String SWITCH_NOW_KEY = "commands.forge.loadworld.switch_now";
    private static final String DELAY_KEY = "commands.forge.loadworld.delay";

    @Override
    public String getName()
    {
        return "loadworld";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.forge.loadworld.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0; // TODO change back to 4
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        if(server.isSinglePlayer()) {
            return false;
        }
        return true; // TODO remove method
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(server.isSinglePlayer()) {
            throw new WrongUsageException("commands.forge.loadworld.no_sp");
        }
        if(args.length < 1) {
            throw new WrongUsageException(getUsage(sender));
        }
        String folderName = args[0];

        WorldInfo worldInfo = server.getActiveAnvilConverter().getSaveLoader(folderName, true).loadWorldInfo();
        if(worldInfo == null) {
            throw new WorldNotFoundException(folderName);
        }

        int delayS = 0;
        if(args.length >= 2) {
            delayS = Integer.valueOf(args[1]);
            // TODO handle delay
        }

        int delayMs = delayS * 1000;
        sendDelayMsg(sender, delayMs);
        Thread switchThread = new Thread(() -> {
            waitDelay(delayMs, server);
            switchWorld(server, folderName);
        });
        switchThread.setDaemon(true);
        switchThread.start();
    }

    private void waitDelay(int delay, MinecraftServer server) {
        int[] mentionTimes = {MS_HOUR, MS_MINUTE * 10, MS_MINUTE, MS_SECOND * 30, MS_SECOND * 10};
        try {
            for (int time : mentionTimes) {
                if(delay > time) {
                    int sleep = delay - time;
                    delay -= sleep;
                    LogManager.getLogger().info("waiting: " + sleep);
                    Thread.sleep(sleep);
                    sendDelayMsgToAllPlayers(server, delay);
                }
            }
            LogManager.getLogger().info("waiting: " + delay);
            Thread.sleep(delay);
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(delay > 0) { // wait did not succeed
            waitDelay(delay, server);
        }
    }

    private void sendDelayMsg(ICommandSender sender, int delayMs) {
        sender.sendMessage(getTextComponent(delayMs));
    }

    private void sendDelayMsgToAllPlayers(MinecraftServer server, int delayMs) {
        server.getPlayerList().sendMessage(getTextComponent(delayMs));
    }

    private TextComponentTranslation getTextComponent(int delayMs) {
        return new TextComponentTranslation(delayMs <= 0 ? SWITCH_NOW_KEY : DELAY_KEY, delayMs / 1000);
    }

    private void switchWorld(MinecraftServer server, String folderName) {
        while (server.getCurrentPlayerCount() > 0) {
            callFromMainThread(server, () -> {
                sendDelayMsgToAllPlayers(server, 0);
                for (EntityPlayerMP p : server.getPlayerList().getPlayers()) {
                    p.connection.disconnect(new TextComponentTranslation("commands.forge.loadworld.kick_msg"));
                }
            });
            for(int i = 0; i < 5 && server.getCurrentPlayerCount() > 0; i++) {
                LogManager.getLogger().info("waiting");
                try {
                    Thread.sleep(100 * i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        callFromMainThread(server, () -> {
            server.clearAndSavePlayers();
            server.unloadWorlds();
            server.loadAllWorlds(folderName, folderName, 0, WorldType.DEFAULT, "");
        });
    }

    private void callFromMainThread(MinecraftServer server, Runnable run) {
        server.callFromMainThread(Executors.callable(run));
    }

    /* -----------------------------------------------------------------------------------------------------------------
        Tab Completion handling
     ---------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1 && sender instanceof EntityPlayerMP)
        {
            postTabCompletionsAsync(server, (EntityPlayerMP) sender, args);
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    private void postTabCompletionsAsync(MinecraftServer server, EntityPlayerMP sender, String[] args) {
        Thread queryThread = new Thread(() -> {
            List<String> names = getListOfStringsMatchingLastWord(args, getWorldNamesFromCurrentDirectory(server));
            callFromMainThread(server, () -> sender.connection.sendPacket(new SPacketTabComplete(names.toArray(new String[0]))));
        });
        queryThread.setDaemon(true);
        queryThread.start();
    }

    /**
     * Find all Minecraft map folders in current server directory. Do not call from Main Thread!
     */
    private String[] getWorldNamesFromCurrentDirectory(MinecraftServer server) {
        ISaveFormat anvilConverter = server.getActiveAnvilConverter();
        if(anvilConverter instanceof AnvilSaveConverter) {
            String[] directories = ((AnvilSaveConverter) anvilConverter).savesDirectory.list((dir, name) -> {
                File upperDir = new File(dir, name);
                return upperDir.isDirectory() && new File(upperDir, "level.dat").exists();
            });
            if(directories != null) {
                return directories;
            }
        }
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }
}
