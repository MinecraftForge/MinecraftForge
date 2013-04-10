package cpw.mods.fml.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureStitched;

public class OpenGL43TextureHelper extends TextureHelper {

    public OpenGL43TextureHelper()
    {
//        GL43.
//        glCopyMethod = Class.forName("org.lwjgl.OpenGL")
    }
    @Override
    public void doTextureCopy(Texture atlas, Texture source, int atlasX, int atlasY)
    {
//        System.out.printf("Src: %d Targ: %d, Coords %d %d %d %d\n", source.func_94282_c(), atlas.func_94282_c(), atlasX, atlasY, source.func_94275_d(), source.func_94276_e());
//        GL43.glCopyImageSubData(source.func_94282_c(), GL11.GL_TEXTURE_2D, 0, 0, 0, 0, atlas.func_94282_c(), GL11.GL_TEXTURE_2D, 0, atlasX, atlasY, 0, source.func_94275_d(), source.func_94276_e(), 1);
//        System.out.printf("Err: %x\n", GL11.glGetError());
    }

    @Override
    public void doTextureUpload(TextureStitched source)
    {
//        source.createAndUploadTextures();
    }

}
