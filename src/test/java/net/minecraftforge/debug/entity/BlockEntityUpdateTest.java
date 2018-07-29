/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.entity;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "entityupdateblockedtest", name = "Entity Update Blocked Test", version = "1.0.0", acceptableRemoteVersions = "*")
public class BlockEntityUpdateTest
{
    @Mod.EventHandler
    public void init(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new BlockEntityUpdateCommand());
    }

    private class BlockEntityUpdateCommand extends CommandBase
    {

        @Override
        public String getName()
        {
            return "blockEntityUpdate";
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return "blockEntityUpdate <value>";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            if (args.length != 1)
            {
                return;
            }
            boolean value = Boolean.parseBoolean(args[0]);
            for (Entity ent : sender.getEntityWorld().loadedEntityList)
            {
                if (!(ent instanceof EntityPlayer))
                {
                    ent.updateBlocked = value;
                }
            }
        }
    }
}
