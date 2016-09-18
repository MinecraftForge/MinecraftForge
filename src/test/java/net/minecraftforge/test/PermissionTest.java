package net.minecraftforge.test;

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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;
import net.minecraftforge.server.permission.context.ContextKey;

@Mod(modid = PermissionTest.MOD_ID, name = "PermissionTest", version = "1.0.0")
public class PermissionTest
{
    public static final String MOD_ID = "PermissionTest";

    @Mod.Instance(PermissionTest.MOD_ID)
    public static PermissionTest inst;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
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
        public static final String CLAIM_CHUNK = PermissionAPI.registerNode("testmod.chunk.claim", DefaultPermissionLevel.ALL, "Node for claiming chunks");
        public static final String UNCLAIM_CHUNK = PermissionAPI.registerNode("testmod.chunk.unclaim", DefaultPermissionLevel.ALL, "Node for unclaiming chunks");
        public static final String SET_BLOCK = PermissionAPI.registerNode("testmod.block.set", DefaultPermissionLevel.OP, "Node for setting blocks with a command");
        public static final String READ_TILE = PermissionAPI.registerNode("testmod.tileentity.read", DefaultPermissionLevel.NONE, "Node for reading and printing TileEntity data");

        public static void init()
        {
        }
    }

    public static class ContextKeys
    {
        public static final ContextKey<TileEntity> TILE_ENTITY = ContextKey.create("tile_entity", TileEntity.class);
    }

    public static class CommandPermissionTest extends CommandBase
    {
        @Override
        public String getCommandName()
        {
            return "permission_test";
        }

        @Override
        public String getCommandUsage(ICommandSender sender)
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

            if(args.length < 1)
            {
                sender.addChatMessage(new TextComponentString("claim, unclaim, setblock, read_tile"));
                return;
            }

            //Example using BlockPosContext with ChunkPos and permission available to players by default
            boolean b = args[0].equals("claim");
            if(b || args[0].equals("unclaim"))
            {
                ChunkPos chunkPos = new ChunkPos(parseInt(args[1]), parseInt(args[2]));

                if(PermissionAPI.hasPermission(player.getGameProfile(), b ? Permissions.CLAIM_CHUNK : Permissions.UNCLAIM_CHUNK, new BlockPosContext(player, chunkPos)))
                {
                    if(b)
                    {
                        //claim chunk
                        sender.addChatMessage(new TextComponentString("Chunk claimed!"));
                    }
                    else
                    {
                        //unclaim chunk
                        sender.addChatMessage(new TextComponentString("Chunk unclaimed!"));
                    }
                }
                else
                {
                    throw new CommandException("commands.generic.permission");
                }
            }
            //Example using BlockPosContext and permission available to only OPs by default
            else if(args[0].equals("setblock"))
            {
                if(args.length < 5)
                {
                    throw new WrongUsageException("commands.setblock.usage");
                }

                BlockPos blockpos = parseBlockPos(sender, args, 1, false);
                Block block = CommandBase.getBlockByText(sender, args[4]);
                int i = 0;

                if(args.length >= 6)
                {
                    i = parseInt(args[5], 0, 15);
                }

                if(!player.worldObj.isBlockLoaded(blockpos))
                {
                    throw new CommandException("commands.setblock.outOfWorld");
                }
                else
                {
                    IBlockState state = block.getStateFromMeta(i);

                    if(!PermissionAPI.hasPermission(player.getGameProfile(), Permissions.SET_BLOCK, new BlockPosContext(player, blockpos, state, null)))
                    {
                        throw new CommandException("commands.generic.permission");
                    }
                    else if(!player.worldObj.setBlockState(blockpos, state, 2))
                    {
                        throw new CommandException("commands.setblock.noChange");
                    }
                }
            }
            //Example using custom ContextKey and permission available to only OPs by default
            else if(args[0].equals("read_tile"))
            {
                BlockPos blockpos = parseBlockPos(sender, args, 1, false);
                TileEntity tileEntity = player.worldObj.getTileEntity(blockpos);

                if(PermissionAPI.hasPermission(player.getGameProfile(), Permissions.READ_TILE, new BlockPosContext(player, blockpos, null, null).set(ContextKeys.TILE_ENTITY, tileEntity)))
                {
                    NBTTagCompound tag = tileEntity == null ? null : tileEntity.serializeNBT();
                    sender.addChatMessage(new TextComponentString(String.valueOf(tag)));
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