package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class TextureMap extends AbstractTexture implements ITickableTextureObject, IIconRegister
{
    private static final Logger field_147635_d = LogManager.getLogger();
    public static final ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation locationItemsTexture = new ResourceLocation("textures/atlas/items.png");
    private final List listAnimatedSprites = Lists.newArrayList();
    private final Map mapRegisteredSprites = Maps.newHashMap();
    private final Map mapUploadedSprites = Maps.newHashMap();
    // JAVADOC FIELD $$ field_94255_a
    private final int textureType;
    private final String basePath;
    private int field_147636_j;
    private int field_147637_k = 1;
    private final TextureAtlasSprite missingImage = new TextureAtlasSprite("missingno");
    private static final String __OBFID = "CL_00001058";

    public TextureMap(int par1, String par2Str)
    {
        this.textureType = par1;
        this.basePath = par2Str;
        this.registerIcons();
    }

    private void initMissingImage()
    {
        int[] aint;

        if ((float)this.field_147637_k > 1.0F)
        {
            boolean flag = true;
            boolean flag1 = true;
            boolean flag2 = true;
            this.missingImage.setIconWidth(32);
            this.missingImage.setIconHeight(32);
            aint = new int[1024];
            System.arraycopy(TextureUtil.missingTextureData, 0, aint, 0, TextureUtil.missingTextureData.length);
            TextureUtil.func_147948_a(aint, 16, 16, 8);
        }
        else
        {
            aint = TextureUtil.missingTextureData;
            this.missingImage.setIconWidth(16);
            this.missingImage.setIconHeight(16);
        }

        int[][] aint1 = new int[this.field_147636_j + 1][];
        aint1[0] = aint;
        this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][] {aint1}));
    }

    public void loadTexture(IResourceManager par1ResourceManager) throws IOException
    {
        this.initMissingImage();
        this.func_147631_c();
        this.loadTextureAtlas(par1ResourceManager);
    }

    public void loadTextureAtlas(IResourceManager par1ResourceManager)
    {
        registerIcons(); //Re-gather list of Icons, allows for addition/removal of blocks/items after this map was initially constructed.

        int i = Minecraft.getGLMaximumTextureSize();
        Stitcher stitcher = new Stitcher(i, i, true, 0, this.field_147636_j);
        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();
        int j = Integer.MAX_VALUE;
        ForgeHooksClient.onTextureStitchedPre(this);
        Iterator iterator = this.mapRegisteredSprites.entrySet().iterator();
        TextureAtlasSprite textureatlassprite;

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();
            ResourceLocation resourcelocation = new ResourceLocation((String)entry.getKey());
            textureatlassprite = (TextureAtlasSprite)entry.getValue();
            ResourceLocation resourcelocation1 = this.func_147634_a(resourcelocation, 0);

            if (textureatlassprite.hasCustomLoader(par1ResourceManager, resourcelocation))
            {
                if (!textureatlassprite.load(par1ResourceManager, resourcelocation))
                {
                    j = Math.min(j, Math.min(textureatlassprite.getIconWidth(), textureatlassprite.getIconHeight()));
                    stitcher.addSprite(textureatlassprite);
                }
                continue;
            }

            try
            {
                IResource iresource = par1ResourceManager.getResource(resourcelocation1);
                BufferedImage[] abufferedimage = new BufferedImage[1 + this.field_147636_j];
                abufferedimage[0] = ImageIO.read(iresource.getInputStream());
                TextureMetadataSection texturemetadatasection = (TextureMetadataSection)iresource.getMetadata("texture");

                if (texturemetadatasection != null)
                {
                    List list = texturemetadatasection.func_148535_c();
                    int l;

                    if (!list.isEmpty())
                    {
                        int k = abufferedimage[0].getWidth();
                        l = abufferedimage[0].getHeight();

                        if (MathHelper.func_151236_b(k) != k || MathHelper.func_151236_b(l) != l)
                        {
                            throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                        }
                    }

                    Iterator iterator3 = list.iterator();

                    while (iterator3.hasNext())
                    {
                        l = ((Integer)iterator3.next()).intValue();

                        if (l > 0 && l < abufferedimage.length - 1 && abufferedimage[l] == null)
                        {
                            ResourceLocation resourcelocation2 = this.func_147634_a(resourcelocation, l);

                            try
                            {
                                abufferedimage[l] = ImageIO.read(par1ResourceManager.getResource(resourcelocation2).getInputStream());
                            }
                            catch (IOException ioexception)
                            {
                                field_147635_d.error("Unable to load miplevel {} from: {}", new Object[] {Integer.valueOf(l), resourcelocation2, ioexception});
                            }
                        }
                    }
                }

                AnimationMetadataSection animationmetadatasection = (AnimationMetadataSection)iresource.getMetadata("animation");
                textureatlassprite.func_147964_a(abufferedimage, animationmetadatasection, (float)this.field_147637_k > 1.0F);
            }
            catch (RuntimeException runtimeexception)
            {
                field_147635_d.error("Unable to parse metadata from " + resourcelocation1, runtimeexception);
                continue;
            }
            catch (IOException ioexception1)
            {
                field_147635_d.error("Using missing texture, unable to load " + resourcelocation1, ioexception1);
                continue;
            }

            j = Math.min(j, Math.min(textureatlassprite.getIconWidth(), textureatlassprite.getIconHeight()));
            stitcher.addSprite(textureatlassprite);
        }

        int i1 = MathHelper.func_151239_c(j);

        if (i1 < this.field_147636_j)
        {
            field_147635_d.debug("{}: dropping miplevel from {} to {}, because of minTexel: {}", new Object[] {this.basePath, Integer.valueOf(this.field_147636_j), Integer.valueOf(i1), Integer.valueOf(j)});
            this.field_147636_j = i1;
        }

        Iterator iterator1 = this.mapRegisteredSprites.values().iterator();

        while (iterator1.hasNext())
        {
            final TextureAtlasSprite textureatlassprite1 = (TextureAtlasSprite)iterator1.next();

            try
            {
                textureatlassprite1.func_147963_d(this.field_147636_j);
            }
            catch (Throwable throwable1)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Applying mipmap");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
                crashreportcategory.addCrashSectionCallable("Sprite name", new Callable()
                {
                    private static final String __OBFID = "CL_00001059";
                    public String call()
                    {
                        return textureatlassprite1.getIconName();
                    }
                });
                crashreportcategory.addCrashSectionCallable("Sprite size", new Callable()
                {
                    private static final String __OBFID = "CL_00001060";
                    public String call()
                    {
                        return textureatlassprite1.getIconWidth() + " x " + textureatlassprite1.getIconHeight();
                    }
                });
                crashreportcategory.addCrashSectionCallable("Sprite frames", new Callable()
                {
                    private static final String __OBFID = "CL_00001061";
                    public String call()
                    {
                        return textureatlassprite1.getFrameCount() + " frames";
                    }
                });
                crashreportcategory.addCrashSection("Mipmap levels", Integer.valueOf(this.field_147636_j));
                throw new ReportedException(crashreport);
            }
        }

        this.missingImage.func_147963_d(this.field_147636_j);
        stitcher.addSprite(this.missingImage);

        try
        {
            stitcher.doStitch();
        }
        catch (StitcherException stitcherexception)
        {
            throw stitcherexception;
        }

        field_147635_d.info("Created: {}x{} {}-atlas", new Object[] {Integer.valueOf(stitcher.getCurrentWidth()), Integer.valueOf(stitcher.getCurrentHeight()), this.basePath});
        TextureUtil.func_147946_a(this.getGlTextureId(), this.field_147636_j, stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), (float)this.field_147637_k);
        HashMap hashmap = Maps.newHashMap(this.mapRegisteredSprites);
        Iterator iterator2 = stitcher.getStichSlots().iterator();

        while (iterator2.hasNext())
        {
            textureatlassprite = (TextureAtlasSprite)iterator2.next();
            String s = textureatlassprite.getIconName();
            hashmap.remove(s);
            this.mapUploadedSprites.put(s, textureatlassprite);

            try
            {
                TextureUtil.func_147955_a(textureatlassprite.func_147965_a(0), textureatlassprite.getIconWidth(), textureatlassprite.getIconHeight(), textureatlassprite.getOriginX(), textureatlassprite.getOriginY(), false, false);
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
                CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Texture being stitched together");
                crashreportcategory1.addCrashSection("Atlas path", this.basePath);
                crashreportcategory1.addCrashSection("Sprite", textureatlassprite);
                throw new ReportedException(crashreport1);
            }

            if (textureatlassprite.hasAnimationMetadata())
            {
                this.listAnimatedSprites.add(textureatlassprite);
            }
            else
            {
                textureatlassprite.clearFramesTextureData();
            }
        }

        iterator2 = hashmap.values().iterator();

        while (iterator2.hasNext())
        {
            textureatlassprite = (TextureAtlasSprite)iterator2.next();
            textureatlassprite.copyFrom(this.missingImage);
        }
        ForgeHooksClient.onTextureStitchedPost(this);
    }

    private ResourceLocation func_147634_a(ResourceLocation p_147634_1_, int p_147634_2_)
    {
        return p_147634_2_ == 0 ? new ResourceLocation(p_147634_1_.getResourceDomain(), String.format("%s/%s%s", new Object[] {this.basePath, p_147634_1_.getResourcePath(), ".png"})): new ResourceLocation(p_147634_1_.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", new Object[] {this.basePath, p_147634_1_.getResourcePath(), Integer.valueOf(p_147634_2_), ".png"}));
    }

    private void registerIcons()
    {
        this.mapRegisteredSprites.clear();
        Iterator iterator;

        if (this.textureType == 0)
        {
            iterator = Block.field_149771_c.iterator();

            while (iterator.hasNext())
            {
                Block block = (Block)iterator.next();

                if (block.func_149688_o() != Material.field_151579_a)
                {
                    block.func_149651_a(this);
                }
            }

            Minecraft.getMinecraft().renderGlobal.registerDestroyBlockIcons(this);
            RenderManager.instance.updateIcons(this);
        }

        iterator = Item.field_150901_e.iterator();

        while (iterator.hasNext())
        {
            Item item = (Item)iterator.next();

            if (item != null && item.getSpriteNumber() == this.textureType)
            {
                item.registerIcons(this);
            }
        }
    }

    public TextureAtlasSprite getAtlasSprite(String par1Str)
    {
        TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)this.mapUploadedSprites.get(par1Str);

        if (textureatlassprite == null)
        {
            textureatlassprite = this.missingImage;
        }

        return textureatlassprite;
    }

    public void updateAnimations()
    {
        TextureUtil.bindTexture(this.getGlTextureId());
        Iterator iterator = this.listAnimatedSprites.iterator();

        while (iterator.hasNext())
        {
            TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)iterator.next();
            textureatlassprite.updateAnimation();
        }
    }

    public IIcon registerIcon(String par1Str)
    {
        if (par1Str == null)
        {
            throw new IllegalArgumentException("Name cannot be null!");
        }
        else if (par1Str.indexOf(92) == -1) // Disable backslashes (\) in texture asset paths.
        {
            Object object = (TextureAtlasSprite)this.mapRegisteredSprites.get(par1Str);

            if (object == null)
            {
                if (this.textureType == 1)
                {
                    if ("clock".equals(par1Str))
                    {
                        object = new TextureClock(par1Str);
                    }
                    else if ("compass".equals(par1Str))
                    {
                        object = new TextureCompass(par1Str);
                    }
                    else
                    {
                        object = new TextureAtlasSprite(par1Str);
                    }
                }
                else
                {
                    object = new TextureAtlasSprite(par1Str);
                }

                this.mapRegisteredSprites.put(par1Str, object);
            }

            return (IIcon)object;
        }
        else
        {
            throw new IllegalArgumentException("Name cannot contain slashes!");
        }
    }

    public int getTextureType()
    {
        return this.textureType;
    }

    public void tick()
    {
        this.updateAnimations();
    }

    public void func_147633_a(int p_147633_1_)
    {
        this.field_147636_j = p_147633_1_;
    }

    public void func_147632_b(int p_147632_1_)
    {
        this.field_147637_k = p_147632_1_;
    }

    //===================================================================================================
    //                                           Forge Start
    //===================================================================================================
    /**
     * Grabs the registered entry for the specified name, returning null if there was not a entry.
     * Opposed to registerIcon, this will not instantiate the entry, useful to test if a mapping exists.
     *
     * @param name The name of the entry to find
     * @return The registered entry, null if nothing was registered.
     */
    public TextureAtlasSprite getTextureExtry(String name)
    {
        return (TextureAtlasSprite)mapRegisteredSprites.get(name);
    }

    /**
     * Adds a texture registry entry to this map for the specified name if one does not already exist.
     * Returns false if the map already contains a entry for the specified name.
     *
     * @param name Entry name
     * @param entry Entry instance
     * @return True if the entry was added to the map, false otherwise.
     */
    public boolean setTextureEntry(String name, TextureAtlasSprite entry)
    {
        if (!mapRegisteredSprites.containsKey(name))
        {
            mapRegisteredSprites.put(name, entry);
            return true;
        }
        return false;
    }
}