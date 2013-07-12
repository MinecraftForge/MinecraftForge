package net.minecraftforge.client.model.techne;

<<<<<<< HEAD
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Resource;
=======
>>>>>>> master
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.IModelCustomLoader;
import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.client.model.obj.WavefrontObject;

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
    public IModelCustom loadInstance(String resourceName, ResourceLocation resource) throws ModelFormatException
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
