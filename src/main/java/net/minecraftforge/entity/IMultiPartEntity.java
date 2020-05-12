package net.minecraftforge.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public interface IMultiPartEntity {
    List<Entity> getParts();
    default void spawnPartsServer(ServerWorld world){
        for(Entity e : getParts()) {
            world.entitiesById.put(e.getEntityId(), e);
        }
    }
    @OnlyIn(Dist.CLIENT)
    default void spawnPartsClient(int parentEntityId){
        List<Entity> parts = getParts();
        for(int i=0; i< parts.size();++i){
            parts.get(i).setEntityId(i + parentEntityId);
        }
    }
    default void removeParts(boolean keepData){
        for (Entity e : getParts()) {
            e.remove(keepData);
        }
    }
    @OnlyIn(Dist.CLIENT)
    default void renderDebugBoundingBoxes(MatrixStack matrixStackIn, IVertexBuilder bufferIn, Entity entityIn, float partialTicks){
        double d0 = entityIn.getPosX() - MathHelper.lerp((double)partialTicks, entityIn.lastTickPosX, entityIn.getPosX());
        double d1 = entityIn.getPosY() - MathHelper.lerp((double)partialTicks, entityIn.lastTickPosY, entityIn.getPosY());
        double d2 = entityIn.getPosZ() - MathHelper.lerp((double)partialTicks, entityIn.lastTickPosZ, entityIn.getPosZ());

        for(Entity part : getParts()) {
            matrixStackIn.push();
            double d3 = d0 + MathHelper.lerp((double)partialTicks, part.lastTickPosX, part.getPosX());
            double d4 = d1 + MathHelper.lerp((double)partialTicks, part.lastTickPosY, part.getPosY());
            double d5 = d2 + MathHelper.lerp((double)partialTicks, part.lastTickPosZ, part.getPosZ());
            matrixStackIn.translate(d3, d4, d5);
            Minecraft.getInstance().getRenderManager().renderBoundingBox(matrixStackIn, bufferIn, part, 0.25F, 1.0F, 0.0F);
            matrixStackIn.pop();
        }
    }
    default void collideWithParts(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, @Nullable Predicate<? super Entity> filter){
        for(Entity part : getParts()) {
            if (part != entityIn && part.getBoundingBox().intersects(aabb) && (filter == null || filter.test(part))) {
                listToFill.add(part);
            }
        }
    }
}
