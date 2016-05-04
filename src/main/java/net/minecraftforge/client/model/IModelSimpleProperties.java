package net.minecraftforge.client.model;

/**
 * Implement this if the model can process "smooth_lighting" or "gui3d" attributes from the json.
 */
public interface IModelSimpleProperties extends IModel
{
    IModel smoothLighting(boolean value);
    IModel gui3d(boolean value);
}
