package net.minecraftforge.debug;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

@Mod(modid = TextureDump.MODID, name = "Forge Texture Atlas Dump", version = TextureDump.VERSION, clientSideOnly = true)
public class TextureDump
{
    public static final String MODID = "forge_texture_dump";
    public static final String VERSION = "1.0";

    public static final boolean ENABLE = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            logger = event.getModLog();
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void postTextureStitch(TextureStitchEvent.Post e) throws Exception
    {
        TextureMap map = e.getMap();
        String name = map.getBasePath().replace('/', '_');
        int mip = map.getMipmapLevels();
        saveGlTexture(name, map.getGlTextureId(), mip);
    }

    public static void saveGlTexture(String name, int textureId, int mipmapLevels)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        for (int level = 0; level <= mipmapLevels; level++)
        {
            int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_WIDTH);
            int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_HEIGHT);
            int size = width * height;

            BufferedImage bufferedimage = new BufferedImage(width, height, 2);
            File output = new File("texture_atlas_dump_" + name + "_mipmap_" + level + ".png");

            IntBuffer buffer = BufferUtils.createIntBuffer(size);
            int[] data = new int[size];

            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, level, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
            buffer.get(data);
            bufferedimage.setRGB(0, 0, width, height, data, 0, width);

            try
            {
                ImageIO.write(bufferedimage, "png", output);
                logger.info("Exported png to: {}", output.getAbsolutePath());
            }
            catch (IOException ioexception)
            {
                logger.info("Unable to write: ", ioexception);
            }
        }
    }
}
