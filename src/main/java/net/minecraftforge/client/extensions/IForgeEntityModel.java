package net.minecraftforge.client.extensions;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.model.animation.EntityModelAnimationModifier;

import java.util.HashSet;
import java.util.Set;

public interface IForgeEntityModel<T extends Entity>
{
    Set<EntityModelAnimationModifier> animationModifiers = new HashSet<>();

    default void prepareAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_){
        setupAnim(p_102618_, p_102619_, p_102620_, p_102621_, p_102622_, p_102623_);
        ModelPart root = getModelRoot();

        for(EntityModelAnimationModifier modifier : animationModifiers)
            modifier.modifyAnimations(p_102618_, root);
    }

    void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_);

    default void addAnimationModifier(EntityModelAnimationModifier<T> modifier){
        this.animationModifiers.add(modifier);
    }

    ModelPart getModelRoot();
}
