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
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.TextComponentHelper;

class CommandWorldCancel extends AbstractWorldCommand
{

    CommandWorldCancel(CommandWorldBase base)
    {
        super(base);
    }

    @Override
    public String getName()
    {
        return "cancel";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.forge.loadworld.cancel.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (base.isWorldJobScheduled())
        {
            base.setJob(null);
            sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.loadworld.cancel.confirm"));
        }
        else
        {
            sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.loadworld.cancel.no_job"));
        }
    }
}
