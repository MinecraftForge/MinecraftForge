package net.minecraftforge.client.model;

/**
 * Implement this if the model can process "smooth_lighting" or "gui3d" attributes from the json.
 */
public interface IModelSimpleProperties<M extends IModelSimpleProperties<M>> extends IModel
{
    M smoothLighting(boolean value);
    M gui3d(boolean value);
}
