package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableMap;

public class ModelProcessingHelper
{
    public static IModel retexture(IModel model, ImmutableMap<String, String> textures)
    {
        if(model instanceof IRetexturableModel)
        {
            model = ((IRetexturableModel)model).retexture(textures);
        }
        return model;
    }

    public static IModel customData(IModel model, ImmutableMap<String, String> customData)
    {
        if(model instanceof IModelCustomData)
        {
            model = ((IModelCustomData)model).process(customData);
        }
        return model;
    }

    public static IModel smoothLighting(IModel model, boolean smooth)
    {
        if(model instanceof IModelSimpleProperties)
        {
            model = ((IModelSimpleProperties)model).smoothLighting(smooth);
        }
        return model;
    }

    public static IModel gui3d(IModel model, boolean gui3d)
    {
        if(model instanceof IModelSimpleProperties)
        {
            model = ((IModelSimpleProperties)model).gui3d(gui3d);
        }
        return model;
    }

    public static IModel uvlock(IModel model, boolean uvlock)
    {
        if(model instanceof IModelUVLock)
        {
            model = ((IModelUVLock)model).uvlock(uvlock);
        }
        return model;
    }
}
