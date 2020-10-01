package net.minecraftforge.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public interface IPOITTeleporter extends ITeleporter
{
    /**
     * A default implementation that uses the vanilla {@link Teleporter} logic to find a portal using a {@link PointOfInterestType}.
     */
    default Optional<TeleportationRepositioner.Result> findPortal(ServerWorld fromWorld, ServerWorld toWorld, Entity entity)
    {
        return new Teleporter(toWorld).func_242957_a(TeleporterHelper.getScaledPos(fromWorld, toWorld, new BlockPos(entity.getPositionVec())), TeleporterHelper.getPortalSearchRadius(fromWorld, toWorld), this.getPortalPOI());
    }
    
    /**
     * The {@link PointOfInterestType} to look for to find a portal.
     */
    PointOfInterestType getPortalPOI();
}
