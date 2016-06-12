package net.minecraftforge.common.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class RayTraceUtils
{
    
    public static Vec3d getStart(EntityPlayer player)
    {
        return new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
    }
    
    public static Vec3d getEnd(EntityPlayer player)
    {
        double reachDistance = player instanceof EntityPlayerMP ? ((EntityPlayerMP) player).interactionManager.getBlockReachDistance()
                : (player.capabilities.isCreativeMode ? 5.0D : 4.5D);
        Vec3d lookVec = player.getLookVec();
        Vec3d start = getStart(player);
        return start.addVector(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance);
    }
    
    public static RayTraceResult rayTrace(EntityPlayer player)
    {
        return rayTrace(player.worldObj, player);
    }
    
    public static RayTraceResult rayTrace(EntityPlayer player, boolean stopOnLiquid)
    {
        return rayTrace(player.worldObj, player, stopOnLiquid);
    }
    
    public static RayTraceResult rayTrace(EntityPlayer player, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox,
            boolean returnLastUncollidableBlock)
    {
        return rayTrace(player.worldObj, player, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
    }
    
    public static RayTraceResult rayTrace(World world, EntityPlayer player)
    {
        return rayTrace(world, player, false);
    }
    
    public static RayTraceResult rayTrace(World world, EntityPlayer player, boolean stopOnLiquid)
    {
        return rayTrace(world, player, stopOnLiquid, false, false);
    }
    
    public static RayTraceResult rayTrace(World world, EntityPlayer player, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox,
            boolean returnLastUncollidableBlock)
    {
        return world.rayTraceBlocks(getStart(player), getEnd(player), stopOnLiquid, ignoreBlockWithoutBoundingBox,
                returnLastUncollidableBlock);
    }
    
}