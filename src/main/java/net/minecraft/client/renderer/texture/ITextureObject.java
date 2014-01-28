package net.minecraft.client.renderer.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.client.resources.IResourceManager;

@SideOnly(Side.CLIENT)
public interface ITextureObject
{
    void loadTexture(IResourceManager var1) throws IOException;

    int getGlTextureId();
}