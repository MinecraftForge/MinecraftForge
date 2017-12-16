package net.minecraftforge.server.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class CommandSetDimension extends CommandBase
{
    @Override
    public String getName()
    {
        return "setdimension";
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("setdim");
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.forge.setdim.usage";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length > 2 && args.length <= 5)
        {
            return getTabCompletionCoordinate(args, 2, targetPos);
        }
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        // args: <entity> <dim> [<x> <y> <z>]
        if (args.length != 2 && args.length != 5)
        {
            throw new WrongUsageException("commands.forge.setdim.usage");
        }
        Entity entity = getEntity(server, sender, args[0]);
        if (!checkEntity(entity))
        {
            throw new CommandException("commands.forge.setdim.invalid.entity", entity.getName());
        }
        int dimension = parseInt(args[1]);
        if (!DimensionManager.isDimensionRegistered(dimension))
        {
            throw new CommandException("commands.forge.setdim.invalid.dim", dimension);
        }
        BlockPos pos = args.length == 5 ? parseBlockPos(sender, args, 2, false) : sender.getPosition();
        WorldServer destination = server.getWorld(dimension);
        Teleporter teleporter = CommandTeleporter.get(destination, pos);

        // fire dimension change event and check result
        if (!ForgeHooks.onTravelToDimension(entity, dimension))
        {
            throw new CommandException("commands.forge.setdim.rejected");
        }

        if (entity instanceof EntityPlayerMP)
        {
            server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) entity, dimension, teleporter);
        }
        else
        {
            // handle non-player-specific tasks performed by transferPlayerToDimension
            int prev = entity.dimension;
            WorldServer from = server.getWorld(entity.dimension);
            entity.dimension = dimension;
            WorldServer to = server.getWorld(entity.dimension);
            from.removeEntityDangerously(entity);
            entity.isDead = false;

            server.getPlayerList().transferEntityToWorld(entity, prev, from, to, teleporter);
        }
    }

    private static boolean checkEntity(Entity entity)
    {
        // use vanilla portal logic, try to avoid doing anything too silly
        return !entity.isRiding() && !entity.isBeingRidden() && entity.isNonBoss();
    }

    private static class CommandTeleporter extends Teleporter
    {
        private static final Map<WorldServer, CommandTeleporter> cache = new WeakHashMap<>();

        private BlockPos targetPos = BlockPos.ORIGIN;

        private CommandTeleporter(WorldServer world)
        {
            super(world);
        }

        static CommandTeleporter get(WorldServer world, BlockPos pos)
        {
            return cache.computeIfAbsent(world, CommandTeleporter::new).setTargetPos(pos);
        }

        private CommandTeleporter setTargetPos(BlockPos pos)
        {
            targetPos = pos;
            return this;
        }

        @Override
        public void placeInPortal(Entity entity, float rotationYaw)
        {
            entity.setPosition(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
            // clear out space for entity
            AxisAlignedBB aabb = entity.getEntityBoundingBox();
            BlockPos min = new BlockPos(MathHelper.floor(aabb.minX), MathHelper.floor(aabb.minY), MathHelper.floor(aabb.minZ));
            BlockPos max = new BlockPos(MathHelper.ceil(aabb.maxX),  MathHelper.ceil(aabb.maxY),  MathHelper.ceil(aabb.maxZ));
            for (BlockPos pos : BlockPos.getAllInBoxMutable(min, max))
            {
                world.setBlockToAir(pos);
            }
        }
    }
}
