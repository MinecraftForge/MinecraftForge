package net.minecraftforge.common.model.techne;

import java.net.URL;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.common.model.IModelCustom;
import net.minecraftforge.common.model.IModelCustomLoader;
import net.minecraftforge.common.model.ModelFormatException;

public class TechneModelLoader implements IModelCustomLoader {
    
    @Override
    public String getType()
    {
        return "Techne model";
    }

    private static final String[] types = { "tcn" };
    @Override
    public String[] getSuffixes()
    {
        return types;
    }

    @Override
    public IModelCustom loadInstance(String resourceName, URL resource) throws ModelFormatException
    {
        return new TechneModel(resourceName, resource);
    }

}
