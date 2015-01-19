package net.minecraftforge.client.model.obj.original;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.ResourceLocation;

public interface IInitializeBakedModel {
    void initialize(ItemCameraTransforms cameraTransforms, ResourceLocation icon, ModelManager modelManager);
}
