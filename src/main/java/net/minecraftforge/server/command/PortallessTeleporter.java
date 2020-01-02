package net.minecraftforge.server.command;

import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

public class PortallessTeleporter implements ITeleporter 
{

   @Override
   public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
      return repositionEntity.apply(false);
   }
}
