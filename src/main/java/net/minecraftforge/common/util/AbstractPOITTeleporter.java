package net.minecraftforge.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public abstract class AbstractPOITTeleporter extends AbstractTeleporter
{
    @Override
    public Optional<TeleportationRepositioner.Result> findPortal(ServerWorld fromWorld, ServerWorld toWorld,
            Entity entity)
    {
        return new Teleporter(toWorld).func_242957_a(new BlockPos(entity.getPositionVec()),
                DimensionType.func_242715_a(fromWorld.func_230315_m_(), toWorld.func_230315_m_()) < 1,
                this.getPortalPOI());
    }

    abstract public PointOfInterestType getPortalPOI();
}
