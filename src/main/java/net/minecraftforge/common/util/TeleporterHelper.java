package net.minecraftforge.common.util;

import net.minecraft.block.PortalInfo;
import net.minecraft.block.PortalSize;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.server.ServerWorld;

public class TeleporterHelper
{
    /**
     * Scales the given {@link BlockPos} based on the {@link World}s passed in.
     */
    public static BlockPos getScaledPos(World fromWorld, World toWorld, BlockPos originalPos)
    {
        WorldBorder worldborder = toWorld.getWorldBorder();
        double minX = Math.max(-2.9999872E7D, worldborder.minX() + 16.0D);
        double minZ = Math.max(-2.9999872E7D, worldborder.minZ() + 16.0D);
        double maxX = Math.min(2.9999872E7D, worldborder.maxX() - 16.0D);
        double maxZ = Math.min(2.9999872E7D, worldborder.maxZ() - 16.0D);
        double dimensionScaling = DimensionType.func_242715_a(fromWorld.func_230315_m_(), toWorld.func_230315_m_());
        return new BlockPos(MathHelper.clamp(originalPos.getX() * dimensionScaling, minX, maxX), originalPos.getY(), MathHelper.clamp(originalPos.getZ() * dimensionScaling, minZ, maxZ));
    }

    /**
     * An overload of {@link ITeleporter#getPortalInfo} that takes the entity being teleported as well as the
     * destination world to create a complete {@link PortalInfo}.
     */
    public static PortalInfo getPortalInfo(TeleportationRepositioner.Result tpResult, Entity entity, ServerWorld destWorld)
    {
        Direction.Axis axis1 = Direction.Axis.X;
        Vector3d vector3d = new Vector3d(0.5D, 0.0D, 0.0D);
        return PortalSize.func_242963_a(destWorld, tpResult, axis1, vector3d,
                entity.getSize(entity.getPose()), entity.getMotion(), entity.rotationYaw, entity.rotationPitch);
    }
    
    /**
     * Gets the search radius to look for a portal based on dimension scaling.
     */
    public static int getPortalSearchRadius(World fromWorld, World toWorld)
    {
        return Math.max((int) DimensionType.func_242715_a(fromWorld.func_230315_m_(), toWorld.func_230315_m_()) * 16, 16);
    }
}
