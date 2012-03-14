package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class TexturePackDefault extends TexturePackBase
{
    /**
     * The allocated OpenGL for this TexturePack, or -1 if it hasn't been loaded yet.
     */
    private int texturePackName = -1;
    private BufferedImage texturePackThumbnail;

    public TexturePackDefault()
    {
        this.texturePackFileName = "Default";
        this.firstDescriptionLine = "The default look of Minecraft";

        try
        {
            this.texturePackThumbnail = ImageIO.read(TexturePackDefault.class.getResource("/pack.png"));
        }
        catch (IOException var2)
        {
            var2.printStackTrace();
        }
    }

    /**
     * Unbinds the thumbnail texture for texture pack screen
     */
    public void unbindThumbnailTexture(Minecraft par1Minecraft)
    {
        if (this.texturePackThumbnail != null)
        {
            par1Minecraft.renderEngine.deleteTexture(this.texturePackName);
        }
    }

    /**
     * binds the texture corresponding to the pack's thumbnail image
     */
    public void bindThumbnailTexture(Minecraft par1Minecraft)
    {
        if (this.texturePackThumbnail != null && this.texturePackName < 0)
        {
            this.texturePackName = par1Minecraft.renderEngine.allocateAndSetupTexture(this.texturePackThumbnail);
        }

        if (this.texturePackThumbnail != null)
        {
            par1Minecraft.renderEngine.bindTexture(this.texturePackName);
        }
        else
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1Minecraft.renderEngine.getTexture("/gui/unknown_pack.png"));
        }
    }
}
