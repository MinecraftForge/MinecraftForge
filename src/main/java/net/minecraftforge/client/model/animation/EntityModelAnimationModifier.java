package net.minecraftforge.client.model.animation;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public interface EntityModelAnimationModifier<T extends Entity>
{

    void modifyAnimations(T entity, ModelPart model);

}
