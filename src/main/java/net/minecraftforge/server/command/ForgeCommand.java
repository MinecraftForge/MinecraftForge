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

import net.minecraft.command.ICommand;

public class ForgeCommand extends CommandTreeBase
{
    public ForgeCommand()
    {
        super.addSubcommand(new CommandTps(this));
        super.addSubcommand(new CommandTrack(this));
        super.addSubcommand(new CommandGenerate(this));
        super.addSubcommand(new CommandEntity(this));
        super.addSubcommand(new CommandSetDimension(this));
        super.addSubcommand(new CommandTreeHelp(this));
    }

    @Override
    public String getName()
    {
        return "forge";
    }

    @Override
    public void addSubcommand(ICommand command)
    {
        throw new UnsupportedOperationException("Don't add sub-commands to /forge, create your own command.");
    }
}
