package net.minecraftforge.fml.packs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.minecraft.resources.ResourcePack;

public abstract class DelegatableResourcePack extends ResourcePack
{
    protected DelegatableResourcePack(File resourcePackFileIn)
    {
        super(resourcePackFileIn);
    }

    @Override
    public abstract InputStream getInputStream(String resourcePath) throws IOException;

    @Override
    public abstract boolean resourceExists(String resourcePath);
}
