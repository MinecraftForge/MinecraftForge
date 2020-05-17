package net.minecraftforge.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class MultiPartEntityUtil {
    public static void spawnPartsServer(@Nonnull Entity entityIn,@Nonnull Int2ObjectMap<Entity> entitiesById){
        for(Entity e : entityIn.getParts()) {
            entitiesById.put(e.getEntityId(), e);
        }
    }
    @OnlyIn(Dist.CLIENT)
    public static void spawnPartsClient(@Nonnull Entity entityIn, int parentEntityId){
        PartEntity[] parts = entityIn.getParts();
        for(int i=0; i< parts.length;++i){
            parts[i].setEntityId(i + parentEntityId);
        }
    }
    public static void removeParts(@Nonnull Entity entityIn, boolean keepData){
        for (Entity e : entityIn.getParts()) {
            e.remove(keepData);
        }
    }
    @OnlyIn(Dist.CLIENT)
    public static void renderDebugBoundingBoxes(@Nonnull MatrixStack matrixStackIn, IVertexBuilder bufferIn,@Nonnull Entity entityIn, float partialTicks){
        for(Entity part : entityIn.getParts()) {
            matrixStackIn.push();
            double d3 = MathHelper.lerp((double)partialTicks, part.lastTickPosX, part.getPosX()) - entityIn.getPosX();
            double d4 = MathHelper.lerp((double)partialTicks, part.lastTickPosY, part.getPosY()) - entityIn.getPosY();
            double d5 = MathHelper.lerp((double)partialTicks, part.lastTickPosZ, part.getPosZ()) - entityIn.getPosZ();
            matrixStackIn.translate(d3, d4, d5);
            Minecraft.getInstance().getRenderManager().renderBoundingBox(matrixStackIn, bufferIn, part, 0.25F, 1.0F, 0.0F);
            matrixStackIn.pop();
        }
    }

    public static void collideWithParts(@Nonnull Entity entityIn, AxisAlignedBB aabb,@Nonnull List<Entity> listToFill, @Nullable Predicate<? super Entity> filter){
        for(Entity part : entityIn.getParts()) {
            if (part != entityIn && part.getBoundingBox().intersects(aabb) && (filter == null || filter.test(part))) {
                listToFill.add(part);
            }
        }
    }
}
