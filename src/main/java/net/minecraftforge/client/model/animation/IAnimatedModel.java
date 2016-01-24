package net.minecraftforge.client.model.animation;

import net.minecraftforge.client.model.IModel;

import com.google.common.base.Optional;

/**
 * IModel that has animation data.
 */
public interface IAnimatedModel extends IModel
{
    Optional<? extends IClip> getClip(String name);
}
