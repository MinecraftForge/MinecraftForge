package net.minecraftforge.common.model.obj;

import java.io.InputStream;
import java.net.URL;

import net.minecraftforge.common.model.IModelCustom;
import net.minecraftforge.common.model.IModelCustomLoader;
import net.minecraftforge.common.model.ModelFormatException;

public class ObjModelLoader implements IModelCustomLoader {

    @Override
    public String getType()
    {
        return "OBJ model";
    }

    private static final String[] types = { "obj" };
    @Override
    public String[] getSuffixes()
    {
        return types;
    }

    @Override
    public IModelCustom loadInstance(String resourceName, URL resource) throws ModelFormatException
    {
        return new WavefrontObject(resourceName, resource);
    }

}
