/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.client;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.client.Minecraft;

public class TextureFXManager
{
    private static final TextureFXManager INSTANCE = new TextureFXManager();

    private Minecraft client;

    private Map<Integer,TextureHolder> texturesById = Maps.newHashMap();
    private Map<String, TextureHolder> texturesByName = Maps.newHashMap();

//    private TextureHelper helper;

    void setClient(Minecraft client)
    {
        this.client = client;
    }
//    public BufferedImage loadImageFromTexturePack(RenderEngine renderEngine, String path) throws IOException
//    {
//        InputStream image=client.field_71418_C.func_77292_e().func_77532_a(path);
//        if (image==null) {
//            throw new RuntimeException(String.format("The requested image path %s is not found",path));
//        }
//        BufferedImage result=ImageIO.read(image);
//        if (result==null)
//        {
//            throw new RuntimeException(String.format("The requested image path %s appears to be corrupted",path));
//        }
//        return result;
//    }
//
    public static TextureFXManager instance()
    {
        return INSTANCE;
    }

    public void fixTransparency(BufferedImage loadedImage, String textureName)
    {
        if (textureName.matches("^/mob/.*_eyes.*.png$"))
        {
            for (int x = 0; x < loadedImage.getWidth(); x++) {
                for (int y = 0; y < loadedImage.getHeight(); y++) {
                    int argb = loadedImage.getRGB(x, y);
                    if ((argb & 0xff000000) == 0 && argb != 0) {
                        loadedImage.setRGB(x, y, 0);
                    }
                }
            }
        }
    }
    public void bindTextureToName(String name, int index)
    {
        TextureHolder holder = new TextureHolder();
        holder.textureId = index;
        holder.textureName = name;
        texturesById.put(index,holder);
        texturesByName.put(name,holder);
    }

    public void setTextureDimensions(int index, int j, int k)
    {
        TextureHolder holder = texturesById.get(index);
        if (holder == null)
        {
            return;
        }
        holder.x = j;
        holder.y = k;
    }

    private class TextureHolder {
        private int textureId;
        private String textureName;
        private int x;
        private int y;
    }

    public Dimension getTextureDimensions(String texture)
    {
        return texturesByName.containsKey(texture) ? new Dimension(texturesByName.get(texture).x, texturesByName.get(texture).y) : new Dimension(1,1);
    }


//    public TextureHelper getHelper()
//    {
//        if (helper == null)
//        {
//            ContextCapabilities capabilities = GLContext.getCapabilities();
//            boolean has43 = false;
//            try
//            {
//                has43 = capabilities.getClass().getField("GL_ARB_copy_image").getBoolean(capabilities);
//            }
//            catch (Exception e)
//            {
//                //e.printStackTrace();
//                // NOOP - LWJGL needs updating
//                FMLLog.info("Forge Mod Loader has detected an older LWJGL version, new advanced texture animation features are disabled");
//            }
////            if (has43 && Boolean.parseBoolean(System.getProperty("fml.useGL43","true")))
////            {
////                FMLLog.info("Using the new OpenGL 4.3 advanced capability for animations");
////                helper = new OpenGL43TextureHelper();
////            }
////            else
//            {
//                FMLLog.info("Not using advanced OpenGL 4.3 advanced capability for animations : OpenGL 4.3 is %s", has43 ? "available" : "not available");
////                helper = new CopySubimageTextureHelper();
//            }
//        }
//        return helper;
//    }
}
