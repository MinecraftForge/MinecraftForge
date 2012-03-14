package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class TexturePackCustom extends TexturePackBase
{
    private ZipFile texturePackZipFile;

    /**
     * The allocated OpenGL texture name for this texture pack, or -1 if it hasn't been allocated yet.
     */
    private int texturePackName = -1;
    private BufferedImage texturePackThumbnail;
    private File texturePackFile;

    public TexturePackCustom(File par1File)
    {
        this.texturePackFileName = par1File.getName();
        this.texturePackFile = par1File;
    }

    /**
     * Truncates the specified string to 34 characters in length and returns it.
     */
    private String truncateString(String par1Str)
    {
        if (par1Str != null && par1Str.length() > 34)
        {
            par1Str = par1Str.substring(0, 34);
        }

        return par1Str;
    }

    public void func_6485_a(Minecraft par1Minecraft) throws IOException
    {
        ZipFile var2 = null;
        InputStream var3 = null;

        try
        {
            var2 = new ZipFile(this.texturePackFile);

            try
            {
                var3 = var2.getInputStream(var2.getEntry("pack.txt"));
                BufferedReader var4 = new BufferedReader(new InputStreamReader(var3));
                this.firstDescriptionLine = this.truncateString(var4.readLine());
                this.secondDescriptionLine = this.truncateString(var4.readLine());
                var4.close();
                var3.close();
            }
            catch (Exception var20)
            {
                ;
            }

            try
            {
                var3 = var2.getInputStream(var2.getEntry("pack.png"));
                this.texturePackThumbnail = ImageIO.read(var3);
                var3.close();
            }
            catch (Exception var19)
            {
                ;
            }

            var2.close();
        }
        catch (Exception var21)
        {
            var21.printStackTrace();
        }
        finally
        {
            try
            {
                var3.close();
            }
            catch (Exception var18)
            {
                ;
            }

            try
            {
                var2.close();
            }
            catch (Exception var17)
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
        if (this.texturePackThumbnail != null)
        {
            par1Minecraft.renderEngine.deleteTexture(this.texturePackName);
        }

        this.closeTexturePackFile();
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

    public void func_6482_a()
    {
        try
        {
            this.texturePackZipFile = new ZipFile(this.texturePackFile);
        }
        catch (Exception var2)
        {
            ;
        }
    }

    /**
     * Closes the zipfile associated to this texture pack. Does nothing for the default texture pack.
     */
    public void closeTexturePackFile()
    {
        try
        {
            this.texturePackZipFile.close();
        }
        catch (Exception var2)
        {
            ;
        }

        this.texturePackZipFile = null;
    }

    /**
     * Gives a texture resource as InputStream.
     */
    public InputStream getResourceAsStream(String par1Str)
    {
        try
        {
            ZipEntry var2 = this.texturePackZipFile.getEntry(par1Str.substring(1));

            if (var2 != null)
            {
                return this.texturePackZipFile.getInputStream(var2);
            }
        }
        catch (Exception var3)
        {
            ;
        }

        return TexturePackBase.class.getResourceAsStream(par1Str);
    }
}
