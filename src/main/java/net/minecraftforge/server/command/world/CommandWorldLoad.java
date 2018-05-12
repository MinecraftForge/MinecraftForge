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

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

class CommandWorldLoad extends AbstractWorldCommand
{
    CommandWorldLoad(CommandWorldBase base)
    {
        super(base);
    }

    @Override
    public String getName()
    {
        return "load";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.forge.loadworld.load.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(getUsage(sender));
        }
        String folderName = args[0];

        int delayS = 0;
        if (args.length >= 2)
        {
            delayS = Integer.valueOf(args[1]);
        }

        if (!doesWorldExist(server, folderName))
        {
            throw new CommandException("commands.forge.loadworld.load.not_found", folderName);
        }

        base.setJob(new LoadWorldJob(server, sender, folderName, delayS));
    }

    /* -----------------------------------------------------------------------------------------------------------------
        Tab Completion handling
     ---------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1 && sender instanceof EntityPlayerMP && !server.isSinglePlayer())
        {
            postTabCompletionsAsync(server, (EntityPlayerMP) sender, args);
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    private void postTabCompletionsAsync(MinecraftServer server, EntityPlayerMP sender, String[] args)
    {
        Thread queryThread = new Thread(() ->
        {
            List<String> names = getListOfStringsMatchingLastWord(args, getWorldNamesFromCurrentDirectory(server));
            server.callFromMainThread(Executors.callable(() -> sender.connection.sendPacket(new SPacketTabComplete(names.toArray(new String[0])))));
        });
        queryThread.setDaemon(true);
        queryThread.start();
    }

    /**
     * Find all Minecraft map folders in current server directory. Do not call from Main Thread!
     */
    private String[] getWorldNamesFromCurrentDirectory(MinecraftServer server)
    {
        ISaveFormat anvilConverter = server.getActiveAnvilConverter();
        if (anvilConverter instanceof AnvilSaveConverter)
        {
            String[] directories = ((AnvilSaveConverter) anvilConverter).savesDirectory.list((dir, name) ->
            {
                File upperDir = new File(dir, name);
                return upperDir.isDirectory() && new File(upperDir, "level.dat").exists();
            });
            if (directories != null)
            {
                return directories;
            }
        }
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }
}
