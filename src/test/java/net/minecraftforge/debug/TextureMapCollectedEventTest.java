package net.minecraftforge.debug;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.io.IOException;
import java.util.Arrays;

@Mod(modid = TextureMapCollectedEventTest.MODID, name = "Forge Texture Collected Event Test", version = TextureDump.VERSION, clientSideOnly = true, acceptableRemoteVersions = "*")
public class TextureMapCollectedEventTest
{
    public static final String MODID   = "forge_texture_collected_test";
    public static final String VERSION = "1.0";

    public static final boolean ENABLE = true;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(this);

            if (!TextureDump.ENABLE)
            {
                MinecraftForge.EVENT_BUS.register(new TextureDump());
            }
        }
    }

    @SubscribeEvent
    public void onTextureCollected(TextureStitchEvent.Collected collectedEvent)
    {
        ResourceLocation booklocation = new ResourceLocation("minecraft", "items/book_normal");
        ResourceLocation pickaxelocation = new ResourceLocation("minecraft", "items/iron_pickaxe");

        createTexture(booklocation, collectedEvent.getMap().getTextureExtry(booklocation.toString()), collectedEvent.getMap());
        createTexture(pickaxelocation, collectedEvent.getMap().getTextureExtry(pickaxelocation.toString()), collectedEvent.getMap());
    }

    //Method to generate the texture!
    @Nonnull
    private TextureAtlasSprite createTexture(@Nonnull ResourceLocation baseTexture, @Nonnull TextureAtlasSprite base, @Nonnull TextureMap map)
    {
        String location = baseTexture.toString() + "_holographic";
        TextureAtlasSprite sprite = new HolographicTexture(base, location);

        // stitch new textures
        map.setTextureEntry(sprite);

        return sprite;
    }

    /*
    This class actually generates the holographic texture that is stitched into the map.

    Code created By: OrionDevelopment
    Original code from: TinkersConstruct - SlimeKnights (This variant has nearly no changes to its variant, so credit for this code goes to them!)

    With Help from: Physis, TTFTCUTS.
    Credit were credit is due!
     */
    public static class HolographicTexture extends TextureAtlasSprite
    {

        boolean[] trans;
        boolean[] edge;
        @Nullable
        private TextureAtlasSprite baseTexture;
        private String             backupTextureLocation;
        private String             extra;

        public HolographicTexture(@Nonnull String baseTextureLocation, @Nonnull String spriteName)
        {
            super(spriteName);

            this.baseTexture = null;
            this.backupTextureLocation = baseTextureLocation;
        }

        public HolographicTexture(@Nonnull TextureAtlasSprite baseTexture, @Nonnull String spriteName)
        {
            super(spriteName);

            this.baseTexture = baseTexture;
            this.backupTextureLocation = baseTexture.getIconName();
        }

        /**
         * Function to get the perceptual brightness of a color in int form.
         * <p>
         * Original code from: Physis, TTFTCUTS. (Probs were probs are due!)
         *
         * @param col The color to het the brightness for.
         * @return The brightness of the color.
         */
        public static int getPerceptualBrightness(int col)
        {
            double r = red(col) / 255.0;
            double g = green(col) / 255.0;
            double b = blue(col) / 255.0;

            return getPerceptualBrightness(r, g, b);
        }

        public static int red(int c)
        {
            return (c >> 16) & 0xFF;
        }

        public static int green(int c)
        {
            return (c >> 8) & 0xFF;
        }

        public static int blue(int c)
        {
            return (c) & 0xFF;
        }

        /**
         * Function to get the perceptual brightness of a color in int form.
         * <p>
         * Original code from: Physis, TTFTCUTS. (Probs were probs are due!)
         *
         * @param r The red channel of the color you want the brightness of
         * @param g The green channel of the color you want the brightness of
         * @param b The blue channel of the color you want the brightness of
         * @return The brightness of the color.
         */
        public static int getPerceptualBrightness(double r, double g, double b)
        {

            double brightness = Math.sqrt(0.241 * r * r + 0.691 * g * g + 0.068 * b * b);

            return (int) (brightness * 255);
        }

        public static int alpha(int c)
        {
            return (c >> 24) & 0xFF;
        }

        protected static int mult(int c1, int c2)
        {
            return (int) ((float) c1 * (c2 / 255f));
        }

        @Nonnull
        public TextureAtlasSprite setSuffix(String suffix)
        {
            this.extra = suffix;
            this.baseTexture = null;
            return this;
        }

        @Override
        public boolean hasCustomLoader(@Nullable IResourceManager manager, @Nullable ResourceLocation location)
        {
            return true;
        }

        @Override
        public boolean load(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location)
        {
            this.framesTextureData = Lists.newArrayList();
            this.frameCounter = 0;
            this.tickCounter = 0;

            //Store the pixel data in a array.
            int[][] data;

            //Check if the basetexture is present and loaded
            //Then copy and prepare for modification.
            if (baseTexture != null && baseTexture.getFrameCount() > 0)
            {
                this.copyFrom(baseTexture);
                int[][] original = baseTexture.getFrameTextureData(0);
                data = new int[original.length][];
                for (int i = 0; i < original.length; i++)
                {
                    if (original[i] != null)
                    {
                        data[i] = Arrays.copyOf(original[i], original[i].length);
                    }
                }
            }
            //Whew not the base texture is not loaded or does not exist load the backup texture.
            else
            {
                data = null;
                if (extra != null && !extra.isEmpty())
                {
                    data = backupLoadTexture(new ResourceLocation(backupTextureLocation + "_" + extra), manager);
                }
                if (data == null)
                {
                    data = backupLoadTexture(new ResourceLocation(backupTextureLocation), manager);
                }
            }

            //Modify the texture.
            processData(data);

            //If no data has been loaded before then add the new set of data.
            //Else skip the loaded data as one is already present.
            if (this.framesTextureData.isEmpty())
            {
                this.framesTextureData.add(data);
            }

            return false;
        }

        /**
         * Method used to load a backup texture
         * <p>
         * Loads the texture manually. Same procedure as the TextureMap.
         *
         * @param resourceLocation The resource location to load the texture from.
         * @param resourceManager  The resource manager to load the texture.
         * @return The Sprite used as backup for the original.
         */
        @Nullable
        protected int[][] backupLoadTexture(@Nonnull ResourceLocation resourceLocation, @Nonnull IResourceManager resourceManager)
        {
            if (resourceLocation.equals(TextureMap.LOCATION_MISSING_TEXTURE))
            {
                return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite().getFrameTextureData(0);
            }

            ResourceLocation resourcelocation1 = this.completeResourceLocation(resourceLocation, 0);

            try
            {
                IResource iresource = resourceManager.getResource(resourcelocation1);
                BufferedImage[] abufferedimage = new BufferedImage[1 + 4];
                abufferedimage[0] = TextureUtil.readBufferedImage(iresource.getInputStream());

                this.width = abufferedimage[0].getWidth();
                this.height = abufferedimage[0].getHeight();

                int[][] aint = new int[abufferedimage.length][];
                for (int k = 0; k < abufferedimage.length; ++k)
                {
                    BufferedImage bufferedimage = abufferedimage[k];

                    if (bufferedimage != null)
                    {
                        aint[k] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
                        bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[k], 0, bufferedimage.getWidth());
                    }
                }

                return aint;
            }
            catch (RuntimeException runtimeexception)
            {
                FMLLog.getLogger().error("Unable to parse metadata from " + resourcelocation1, runtimeexception);
            }
            catch (IOException ioexception1)
            {
                FMLLog.getLogger()
                  .error("Unable to generate " + this.getIconName() + ": unable to load " + resourcelocation1 + "!\nBase texture: " + baseTexture.getIconName(), ioexception1);
            }

            return null;
        }

        protected void processData(@Nonnull int[][] data)
        {
            // preprocess
            DirectColorModel color = new DirectColorModel(32, 16711680, '\uff00', 255, -16777216);

            edge = new boolean[width * height];
            trans = new boolean[width * height];

            int y;
            int c;
            for (int x = 0; x < width; ++x)
            {
                for (y = 0; y < height; ++y)
                {
                    if (x == 0 || y == 0 || x == width - 1 || y == height - 1)
                    {
                        edge[coord(x, y)] = true;
                    }

                    c = data[0][coord(x, y)];
                    if (c == 0 || color.getAlpha(c) < 64)
                    {
                        trans[coord(x, y)] = true;
                        if (x > 0)
                        {
                            edge[coord(x - 1, y)] = true;
                        }

                        if (y > 0)
                        {
                            edge[coord(x, y - 1)] = true;
                        }

                        if (x < width - 1)
                        {
                            edge[coord(x + 1, y)] = true;
                        }

                        if (y < height - 1)
                        {
                            edge[coord(x, y + 1)] = true;
                        }
                    }
                }
            }

            //Use the mipmap levels to color the pixels.
            for (int mipmap = 0; mipmap < data.length; mipmap++)
            {
                //If no data exists for that MipMap level skip it.
                if (data[mipmap] == null)
                {
                    continue;
                }

                //Iterate over all pixels in the MipMap level and color them.
                for (int pxCoord = 0; pxCoord < data[mipmap].length; pxCoord++)
                {
                    data[mipmap][pxCoord] = colorPixel(data[mipmap][pxCoord], mipmap, pxCoord);
                }
            }
        }

        /**
         * Function to get a MipMap level depending resource location.
         *
         * @param location    The original location
         * @param mipmapLevel The mipmap level to get the modified version for.
         * @return A location in the root textures directory if 0 is passed in as location or: a location in the mipmaps sub
         * directory with the {mipmaplevel}.png as ending
         */
        @Nonnull
        protected ResourceLocation completeResourceLocation(@Nonnull ResourceLocation location, int mipmapLevel)
        {
            if (mipmapLevel == 0)
            {
                return new ResourceLocation(location.getResourceDomain(),
                                             String.format("%s/%s%s", "textures", location.getResourcePath(), ".png"));
            }

            return new ResourceLocation(location.getResourceDomain(), String
                                                                        .format("%s/mipmaps/%s.%d%s", "textures", location.getResourcePath(), mipmapLevel, ".png"));
        }

        protected int coord(int x, int y)
        {
            return y * width + x;
        }

        protected int colorPixel(int pixel, int mipmap, int pxCoord)
        {
            if (!trans[pxCoord])
            {
                if (edge[pxCoord])
                {
                    return compose(50, 50, 50, 255);
                }
                else
                {
                    return 0;
                }
            }

            return pixel;
        }

        /**
         * Method to merge the 4 channels into a aRGB int representation.
         *
         * @param r The red channel
         * @param g The green channel
         * @param b The blue channel
         * @param a The alpha channel
         * @return The aRGB value in a int.
         */
        public static int compose(int r, int g, int b, int a)
        {
            int rgb = a;
            rgb = (rgb << 8) + r;
            rgb = (rgb << 8) + g;
            rgb = (rgb << 8) + b;
            return rgb;
        }

        // Get coordinates from index and vice versa
        protected int getX(int pxCoord)
        {
            return pxCoord % width;
        }

        protected int getY(int pxCoord)
        {
            return pxCoord / width;
        }
    }
}
