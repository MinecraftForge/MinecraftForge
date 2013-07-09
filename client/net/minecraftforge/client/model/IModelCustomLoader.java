package net.minecraftforge.client.model;

import net.minecraft.client.resources.ResourceLocation;

/**
 * Instances of this class act as factories for their model type
 * 
 * @author cpw
 * 
 */
public interface IModelCustomLoader
{
	/**
	 * Get the main type name for this loader
	 * 
	 * @return the type name
	 */
	String getType();
	
	/**
	 * Get resource suffixes this model loader recognizes
	 * 
	 * @return a list of suffixes
	 */
	String[] getSuffixes();
	
	/**
	 * Load a model instance from the supplied path
	 * 
	 * @param resourceName
	 *            The resource name to load
	 * @param resource
	 *            The URL associated with the classloader resource
	 * @return A model instance
	 * @throws ModelFormatException
	 *             if the model format is not correct
	 */
	IModelCustom loadInstance(String resourceName, ResourceLocation resource) throws ModelFormatException;
}
