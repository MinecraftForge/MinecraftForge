package net.minecraftforge.client.extensions;

import static net.minecraftforge.fml.Logging.MODELLOADING;

import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.logging.ModelLoaderErrorMessage;

public interface IForgeModelBakery
{
    default ModelBakery getBakery()
    {
        return (ModelBakery) this;
    }

    /**
     * Internal, do not use.
     */
    default void onPostBakeEvent(Map<ModelResourceLocation, IBakedModel> modelRegistry)
    {
        final Logger LOGGER = LogManager.getLogger();
        IBakedModel missingModel = modelRegistry.get(ModelBakery.MODEL_MISSING);
        for (Map.Entry<ResourceLocation, Exception> entry : getLoadingExceptions().entrySet())
        {
            // ignoring pure ResourceLocation arguments, all things we care about pass
            // ModelResourceLocation
            if (entry.getKey() instanceof ModelResourceLocation)
            {
                LOGGER.debug(MODELLOADING, () -> new ModelLoaderErrorMessage((ModelResourceLocation) entry.getKey(), entry.getValue()));
                final ModelResourceLocation location = (ModelResourceLocation) entry.getKey();
                final IBakedModel model = modelRegistry.get(location);
                if (model == null)
                {
                    modelRegistry.put(location, missingModel);
                }
            }
        }
        getLoadingExceptions().clear();
    }

    Map<ResourceLocation, Exception> getLoadingExceptions();
}
