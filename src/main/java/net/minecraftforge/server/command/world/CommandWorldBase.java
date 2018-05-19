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
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nullable;

public class CommandWorldBase extends CommandTreeBase
{

    private AbstractWorldJob currentJob;

    public CommandWorldBase()
    {
        super.addSubcommand(new CommandWorldLoad(this));
        super.addSubcommand(new CommandWorldNew(this));
        super.addSubcommand(new CommandWorldCancel(this));
    }

    @Override
    public String getName()
    {
        return "loadworld";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.forge.loadworld.base.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

    @Override
    public void addSubcommand(ICommand command)
    {
        throw new UnsupportedOperationException("Don't add sub-commands to /forge loadworld, create your own command.");
    }

    void setJob(@Nullable AbstractWorldJob job)
    {
        if (currentJob != null)
        {
            currentJob.cancel();
            currentJob = job;
        }
        if (job != null)
        {
            job.start();
        }
    }

    boolean isWorldJobScheduled()
    {
        return currentJob != null && !currentJob.canceled() && currentJob.isAlive();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (server.isSinglePlayer())
        {
            throw new WrongUsageException("commands.forge.loadworld.base.no_sp");
        }
        super.execute(server, sender, args);
    }
}
