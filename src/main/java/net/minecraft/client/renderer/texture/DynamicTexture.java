package net.minecraft.client.renderer.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.resources.IResourceManager;

@SideOnly(Side.CLIENT)
public class DynamicTexture extends AbstractTexture
{
    private final int[] dynamicTextureData;
    // JAVADOC FIELD $$ field_94233_j
    private final int width;
    // JAVADOC FIELD $$ field_94234_k
    private final int height;
    private static final String __OBFID = "CL_00001048";

    public DynamicTexture(BufferedImage par1BufferedImage)
    {
        this(par1BufferedImage.getWidth(), par1BufferedImage.getHeight());
        par1BufferedImage.getRGB(0, 0, par1BufferedImage.getWidth(), par1BufferedImage.getHeight(), this.dynamicTextureData, 0, par1BufferedImage.getWidth());
        this.updateDynamicTexture();
    }

    public DynamicTexture(int par1, int par2)
    {
        this.width = par1;
        this.height = par2;
        this.dynamicTextureData = new int[par1 * par2];
        TextureUtil.allocateTexture(this.getGlTextureId(), par1, par2);
    }

    public void loadTexture(IResourceManager par1ResourceManager) throws IOException {}

    public void updateDynamicTexture()
    {
        TextureUtil.uploadTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height);
    }

    public int[] getTextureData()
    {
        return this.dynamicTextureData;
    }
}