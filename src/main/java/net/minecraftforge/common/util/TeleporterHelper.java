package net.minecraftforge.common.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

public class TeleporterHelper
{
    /**
     * Scales the given {@link BlockPos} based on the {@link World}s passed in.
     */
    public static BlockPos getScaledPos(World fromWorld, World toWorld, BlockPos originalPos)
    {
        WorldBorder worldborder = toWorld.getWorldBorder();
        double maxRadius = 2.9999872E7D;
        int offset = 16;
        double minX = Math.max(-maxRadius, worldborder.minX() + offset);
        double minZ = Math.max(-maxRadius, worldborder.minZ() + offset);
        double maxX = Math.min(maxRadius, worldborder.maxX() - offset);
        double maxZ = Math.min(maxRadius, worldborder.maxZ() - offset);
        double dimensionScaling = DimensionType.func_242715_a(fromWorld.func_230315_m_(), toWorld.func_230315_m_());
        return new BlockPos(MathHelper.clamp(originalPos.getX() * dimensionScaling, minX, maxX), originalPos.getY(), MathHelper.clamp(originalPos.getZ() * dimensionScaling, minZ, maxZ));
    }

    /**
     * Gets the search radius to look for a portal based on dimension scaling.
     * <p>
     * Min search radius is 16.
     */
    public static int getPortalSearchRadius(World fromWorld, World toWorld)
    {
        return Math.max((int) DimensionType.func_242715_a(fromWorld.func_230315_m_(), toWorld.func_230315_m_()) * 16, 16);
    }
}
