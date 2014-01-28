package net.minecraft.client.renderer.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AbstractTexture implements ITextureObject
{
    protected int glTextureId = -1;
    private static final String __OBFID = "CL_00001047";

    public int getGlTextureId()
    {
        if (this.glTextureId == -1)
        {
            this.glTextureId = TextureUtil.glGenTextures();
        }

        return this.glTextureId;
    }

    public void func_147631_c()
    {
        if (this.glTextureId != -1)
        {
            TextureUtil.func_147942_a(this.glTextureId);
            this.glTextureId = -1;
        }
    }
}