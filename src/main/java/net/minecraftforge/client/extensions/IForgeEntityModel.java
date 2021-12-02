package net.minecraftforge.client.extensions;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.model.animation.IEntityModelAnimationModifier;

import java.util.HashSet;
import java.util.Set;

public interface IForgeEntityModel<T extends Entity>
{
    Set<IEntityModelAnimationModifier> animationModifiers = new HashSet<>();

    default void prepareAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_)
    {
        setupAnim(p_102618_, p_102619_, p_102620_, p_102621_, p_102622_, p_102623_);
        ModelPart root = getModelRoot();

        for (IEntityModelAnimationModifier modifier : animationModifiers)
            modifier.modifyAnimations(p_102618_, root, p_102619_, p_102620_, p_102621_, p_102622_, p_102623_);
    }

    void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_);

    default void addAnimationModifier(IEntityModelAnimationModifier<T> modifier)
    {
        this.animationModifiers.add(modifier);
    }

    ModelPart getModelRoot();
}
