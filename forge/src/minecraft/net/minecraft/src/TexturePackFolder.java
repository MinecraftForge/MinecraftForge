package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class TexturePackFolder extends TexturePackBase
{
    private int field_48191_e = -1;
    private BufferedImage field_48189_f;
    private File field_48190_g;

    public TexturePackFolder(File par1File)
    {
        this.texturePackFileName = par1File.getName();
        this.field_48190_g = par1File;
    }

    private String func_48188_b(String par1Str)
    {
        if (par1Str != null && par1Str.length() > 34)
        {
            par1Str = par1Str.substring(0, 34);
        }

        return par1Str;
    }

    public void func_6485_a(Minecraft par1Minecraft) throws IOException
    {
        InputStream var2 = null;

        try
        {
            try
            {
                var2 = this.getResourceAsStream("pack.txt");
                BufferedReader var3 = new BufferedReader(new InputStreamReader(var2));
                this.firstDescriptionLine = this.func_48188_b(var3.readLine());
                this.secondDescriptionLine = this.func_48188_b(var3.readLine());
                var3.close();
                var2.close();
            }
            catch (Exception var15)
            {
                ;
            }

            try
            {
                var2 = this.getResourceAsStream("pack.png");
                this.field_48189_f = ImageIO.read(var2);
                var2.close();
            }
            catch (Exception var14)
            {
                ;
            }
        }
        catch (Exception var16)
        {
            var16.printStackTrace();
        }
        finally
        {
            try
            {
                var2.close();
            }
            catch (Exception var13)
            {
                ;
            }
        }
    }

    /**
     * Unbinds the thumbnail texture for texture pack screen
     */
    public void unbindThumbnailTexture(Minecraft par1Minecraft)
    {
        if (this.field_48189_f != null)
        {
            par1Minecraft.renderEngine.deleteTexture(this.field_48191_e);
        }

        this.closeTexturePackFile();
    }

    /**
     * binds the texture corresponding to the pack's thumbnail image
     */
    public void bindThumbnailTexture(Minecraft par1Minecraft)
    {
        if (this.field_48189_f != null && this.field_48191_e < 0)
        {
            this.field_48191_e = par1Minecraft.renderEngine.allocateAndSetupTexture(this.field_48189_f);
        }

        if (this.field_48189_f != null)
        {
            par1Minecraft.renderEngine.bindTexture(this.field_48191_e);
        }
        else
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1Minecraft.renderEngine.getTexture("/gui/unknown_pack.png"));
        }
    }

    public void func_6482_a() {}

    /**
     * Closes the zipfile associated to this texture pack. Does nothing for the default texture pack.
     */
    public void closeTexturePackFile() {}

    /**
     * Gives a texture resource as InputStream.
     */
    public InputStream getResourceAsStream(String par1Str)
    {
        try
        {
            File var2 = new File(this.field_48190_g, par1Str.substring(1));

            if (var2.exists())
            {
                return new BufferedInputStream(new FileInputStream(var2));
            }
        }
        catch (Exception var3)
        {
            ;
        }

        return TexturePackBase.class.getResourceAsStream(par1Str);
    }
}
