package net.minecraftforge.client.model.techne;

import net.minecraft.client.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.IModelCustomLoader;
import net.minecraftforge.client.model.ModelFormatException;

public class TechneModelLoader implements IModelCustomLoader
{
	
	@Override
	public String getType()
	{
		return "Techne model";
	}
	
	private static final String[]	types	= { "tcn" };
	
	@Override
	public String[] getSuffixes()
	{
		return types;
	}
	
	@Override
	public IModelCustom loadInstance(String resourceName, ResourceLocation resource) throws ModelFormatException
	{
		return new TechneModel(resourceName, resource);
	}
	
}
