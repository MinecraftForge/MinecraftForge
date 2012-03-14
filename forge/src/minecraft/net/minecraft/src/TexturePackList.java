package net.minecraft.src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class TexturePackList
{
    /** The list of the available texture packs. */
    private List availableTexturePacks = new ArrayList();

    /** The default texture pack. */
    private TexturePackBase defaultTexturePack = new TexturePackDefault();

    /** The TexturePack that will be used. */
    public TexturePackBase selectedTexturePack;
    private Map field_6538_d = new HashMap();

    /** The Minecraft instance used by this TexturePackList */
    private Minecraft mc;

    /** The directory the texture packs will be loaded from. */
    private File texturePackDir;
    private String currentTexturePack;

    public TexturePackList(Minecraft par1Minecraft, File par2File)
    {
        this.mc = par1Minecraft;
        this.texturePackDir = new File(par2File, "texturepacks");

        if (!this.texturePackDir.exists())
        {
            this.texturePackDir.mkdirs();
        }

        this.currentTexturePack = par1Minecraft.gameSettings.skin;
        this.updateAvaliableTexturePacks();
        this.selectedTexturePack.func_6482_a();
    }

    /**
     * Sets the new TexturePack to be used, returning true if it has actually changed, false if nothing changed.
     */
    public boolean setTexturePack(TexturePackBase par1TexturePackBase)
    {
        if (par1TexturePackBase == this.selectedTexturePack)
        {
            return false;
        }
        else
        {
            this.selectedTexturePack.closeTexturePackFile();
            this.currentTexturePack = par1TexturePackBase.texturePackFileName;
            this.selectedTexturePack = par1TexturePackBase;
            this.mc.gameSettings.skin = this.currentTexturePack;
            this.mc.gameSettings.saveOptions();
            this.selectedTexturePack.func_6482_a();
            return true;
        }
    }

    /**
     * check the texture packs the client has installed
     */
    public void updateAvaliableTexturePacks()
    {
        ArrayList var1 = new ArrayList();
        this.selectedTexturePack = null;
        var1.add(this.defaultTexturePack);

        if (this.texturePackDir.exists() && this.texturePackDir.isDirectory())
        {
            File[] var2 = this.texturePackDir.listFiles();
            File[] var3 = var2;
            int var4 = var2.length;

            for (int var5 = 0; var5 < var4; ++var5)
            {
                File var6 = var3[var5];
                String var7;
                TexturePackBase var14;

                if (var6.isFile() && var6.getName().toLowerCase().endsWith(".zip"))
                {
                    var7 = var6.getName() + ":" + var6.length() + ":" + var6.lastModified();

                    try
                    {
                        if (!this.field_6538_d.containsKey(var7))
                        {
                            TexturePackCustom var13 = new TexturePackCustom(var6);
                            var13.texturePackID = var7;
                            this.field_6538_d.put(var7, var13);
                            var13.func_6485_a(this.mc);
                        }

                        var14 = (TexturePackBase)this.field_6538_d.get(var7);

                        if (var14.texturePackFileName.equals(this.currentTexturePack))
                        {
                            this.selectedTexturePack = var14;
                        }

                        var1.add(var14);
                    }
                    catch (IOException var10)
                    {
                        var10.printStackTrace();
                    }
                }
                else if (var6.isDirectory() && (new File(var6, "pack.txt")).exists())
                {
                    var7 = var6.getName() + ":folder:" + var6.lastModified();

                    try
                    {
                        if (!this.field_6538_d.containsKey(var7))
                        {
                            TexturePackFolder var8 = new TexturePackFolder(var6);
                            var8.texturePackID = var7;
                            this.field_6538_d.put(var7, var8);
                            var8.func_6485_a(this.mc);
                        }

                        var14 = (TexturePackBase)this.field_6538_d.get(var7);

                        if (var14.texturePackFileName.equals(this.currentTexturePack))
                        {
                            this.selectedTexturePack = var14;
                        }

                        var1.add(var14);
                    }
                    catch (IOException var9)
                    {
                        var9.printStackTrace();
                    }
                }
            }
        }

        if (this.selectedTexturePack == null)
        {
            this.selectedTexturePack = this.defaultTexturePack;
        }

        this.availableTexturePacks.removeAll(var1);
        Iterator var11 = this.availableTexturePacks.iterator();

        while (var11.hasNext())
        {
            TexturePackBase var12 = (TexturePackBase)var11.next();
            var12.unbindThumbnailTexture(this.mc);
            this.field_6538_d.remove(var12.texturePackID);
        }

        this.availableTexturePacks = var1;
    }

    /**
     * Returns a list of the available texture packs.
     */
    public List availableTexturePacks()
    {
        return new ArrayList(this.availableTexturePacks);
    }
}
