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

package net.minecraftforge.debug.mod;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;
import net.minecraftforge.server.permission.context.ContextKey;

@Mod(modid = PermissionTest.MOD_ID, name = "PermissionTest", version = "1.0.0", acceptableRemoteVersions = "*")
public class PermissionTest
{
    public static final String MOD_ID = "permission_test";

    @Mod.Instance(PermissionTest.MOD_ID)
    public static PermissionTest inst;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        Permissions.init();
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandPermissionTest());
    }

    public static class Permissions
    {
        public static final String CLAIM_CHUNK = "testmod.chunk.claim";
        public static final String UNCLAIM_CHUNK = "testmod.chunk.unclaim";
        public static final String SET_BLOCK = "testmod.block.set";
        public static final String READ_TILE = "testmod.tileentity.read";

        public static void init()
        {
            PermissionAPI.registerNode(CLAIM_CHUNK, DefaultPermissionLevel.ALL, "Node for claiming chunks");
            PermissionAPI.registerNode(UNCLAIM_CHUNK, DefaultPermissionLevel.ALL, "Node for unclaiming chunks");
            PermissionAPI.registerNode(SET_BLOCK, DefaultPermissionLevel.OP, "Node for setting blocks with a command");
            PermissionAPI.registerNode(READ_TILE, DefaultPermissionLevel.NONE, "Node for reading and printing TileEntity data");
        }
    }

    public static class ContextKeys
    {
        public static final ContextKey<TileEntity> TILE_ENTITY = ContextKey.create("tile_entity", TileEntity.class);
    }

    public static class CommandPermissionTest extends CommandBase
    {
        @Override
        public String getName()
        {
            return "permission_test";
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return "commands.permission_test.usage";
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender)
        {
            return true;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);

            if (args.length < 1)
            {
                sender.sendMessage(new TextComponentString("claim, unclaim, setblock, read_tile"));
                return;
            }

            //Example using BlockPosContext with ChunkPos and permission available to players by default
            boolean b = args[0].equals("claim");
            if (b || args[0].equals("unclaim"))
            {
                ChunkPos chunkPos = new ChunkPos(parseInt(args[1]), parseInt(args[2]));

                if (PermissionAPI.hasPermission(player.getGameProfile(), b ? Permissions.CLAIM_CHUNK : Permissions.UNCLAIM_CHUNK, new BlockPosContext(player, chunkPos)))
                {
                    if (b)
                    {
                        //claim chunk
                        sender.sendMessage(new TextComponentString("Chunk claimed!"));
                    }
                    else
                    {
                        //unclaim chunk
                        sender.sendMessage(new TextComponentString("Chunk unclaimed!"));
                    }
                }
                else
                {
                    throw new CommandException("commands.generic.permission");
                }
            }
            //Example using BlockPosContext and permission available to only OPs by default
            else if (args[0].equals("setblock"))
            {
                if (args.length < 5)
                {
                    throw new WrongUsageException("commands.setblock.usage");
                }

                BlockPos blockpos = parseBlockPos(sender, args, 1, false);
                Block block = CommandBase.getBlockByText(sender, args[4]);
                int i = 0;

                if (args.length >= 6)
                {
                    i = parseInt(args[5], 0, 15);
                }

                if (!player.world.isBlockLoaded(blockpos))
                {
                    throw new CommandException("commands.setblock.outOfWorld");
                }
                else
                {
                    IBlockState state = block.getStateFromMeta(i);

                    if (!PermissionAPI.hasPermission(player.getGameProfile(), Permissions.SET_BLOCK, new BlockPosContext(player, blockpos, state, null)))
                    {
                        throw new CommandException("commands.generic.permission");
                    }
                    else if (!player.world.setBlockState(blockpos, state, 2))
                    {
                        throw new CommandException("commands.setblock.noChange");
                    }
                }
            }
            //Example using custom ContextKey and permission available to only OPs by default
            else if (args[0].equals("read_tile"))
            {
                BlockPos blockpos = parseBlockPos(sender, args, 1, false);
                TileEntity tileEntity = player.world.getTileEntity(blockpos);

                if (PermissionAPI.hasPermission(player.getGameProfile(), Permissions.READ_TILE, new BlockPosContext(player, blockpos, null, null).set(ContextKeys.TILE_ENTITY, tileEntity)))
                {
                    NBTTagCompound tag = tileEntity == null ? null : tileEntity.serializeNBT();
                    sender.sendMessage(new TextComponentString(String.valueOf(tag)));
                }
                else
                {
                    throw new CommandException("commands.generic.permission");
                }
            }
            else
            {
                throw new CommandException("commands.generic.permission");
            }
        }
    }
}
