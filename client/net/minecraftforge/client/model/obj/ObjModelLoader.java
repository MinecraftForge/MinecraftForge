package net.minecraftforge.client.model.obj;

import net.minecraft.client.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.IModelCustomLoader;
import net.minecraftforge.client.model.ModelFormatException;

public class ObjModelLoader implements IModelCustomLoader
{
	
	@Override
	public String getType()
	{
		return "OBJ model";
	}
	
	private static final String[]	types	= { "obj" };
	
	@Override
	public String[] getSuffixes()
	{
		return types;
	}
	
	@Override
	public IModelCustom loadInstance(String resourceName, ResourceLocation resource) throws ModelFormatException
	{
		return new WavefrontObject(resourceName, resource);
	}
	
}
