package net.minecraftforge.client.model.obj;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Resource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.IModelCustomLoader;
import net.minecraftforge.client.model.ModelFormatException;

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
        InputStream input = null;
        try
        {
            input = resource.openStream();
            return new WavefrontObject(resourceName, input);
        }
        catch (IOException ex)
        {
            throw new ModelFormatException("IO Exception reading model format", ex);
        }
    }

    @Override
    public IModelCustom loadInstance(String resourceName, ResourceLocation resource) throws ModelFormatException
    {
        InputStream input = null;
        try
        {
            Resource res = Minecraft.getMinecraft().func_110442_L().func_110536_a(resource);
            input = res.func_110527_b();
            return new WavefrontObject(resourceName, input);
        }
        catch (IOException ex)
        {
            throw new ModelFormatException("IO Exception reading model format", ex);
        }
    }
}
