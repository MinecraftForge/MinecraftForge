package net.minecraftforge.client.event;

import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.IRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when the ModelManager is notified of the resource manager reloading.
 * Called after model registry is setup, but before it's passed to BlockModelShapes.
 */
public class ModelBakeEvent extends Event
{
    public final ModelManager modelManager;
    public final IRegistry<ModelResourceLocation, IBakedModel> modelRegistry;
    @Deprecated
    public final ModelBakery modelBakery;
    public final ModelLoader modelLoader;

    @Deprecated
    public ModelBakeEvent(ModelManager modelManager, IRegistry<ModelResourceLocation, IBakedModel> modelRegistry, ModelBakery modelBakery)
    {
        this(modelManager, modelRegistry, (ModelLoader)modelBakery);
    }

    public ModelBakeEvent(ModelManager modelManager, IRegistry<ModelResourceLocation, IBakedModel> modelRegistry, ModelLoader modelLoader)
    {
        this.modelManager = modelManager;
        this.modelRegistry = modelRegistry;
        this.modelBakery = modelLoader;
        this.modelLoader = modelLoader;
    }
}
