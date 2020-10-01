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
        double minX = Math.max(-2.9999872E7D, worldborder.minX() + 16.0D);
        double minZ = Math.max(-2.9999872E7D, worldborder.minZ() + 16.0D);
        double maxX = Math.min(2.9999872E7D, worldborder.maxX() - 16.0D);
        double maxZ = Math.min(2.9999872E7D, worldborder.maxZ() - 16.0D);
        double dimensionScaling = DimensionType.func_242715_a(fromWorld.func_230315_m_(), toWorld.func_230315_m_());
        return new BlockPos(MathHelper.clamp(originalPos.getX() * dimensionScaling, minX, maxX), originalPos.getY(), MathHelper.clamp(originalPos.getZ() * dimensionScaling, minZ, maxZ));
    }

    /**
     * Gets the search radius to look for a portal based on dimension scaling.
     */
    public static int getPortalSearchRadius(World fromWorld, World toWorld)
    {
        return Math.max((int) DimensionType.func_242715_a(fromWorld.func_230315_m_(), toWorld.func_230315_m_()) * 16, 16);
    }
}
