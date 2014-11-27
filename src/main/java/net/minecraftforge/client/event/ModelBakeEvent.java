package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.IRegistry;

/**
 * Fired when the ModelManager is notified of the resource manager reloading.
 * Called after model registry is setup, but before it's passed to BlockModelShapes.
 */
public class ModelBakeEvent extends Event
{
    public final ModelManager modelManager;
    public final IRegistry modelRegistry;
    public final ModelBakery modelBakery;

    public ModelBakeEvent(ModelManager modelManager, IRegistry modelRegistry, ModelBakery modelBakery)
    {
        this.modelManager = modelManager;
        this.modelRegistry = modelRegistry;
        this.modelBakery = modelBakery;
    }
}
