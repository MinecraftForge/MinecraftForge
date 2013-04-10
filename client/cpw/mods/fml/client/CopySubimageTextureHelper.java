package cpw.mods.fml.client;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureStitched;

public class CopySubimageTextureHelper extends TextureHelper {
    @Override
    public void doTextureCopy(Texture atlas, Texture source, int atlasX, int atlasY)
    {
        if (atlas.func_94282_c() == -1)
        {
            return;
        }
        atlas.func_94277_a(0);
        ByteBuffer buffer = source.func_94273_h();
        buffer.position(0);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, atlasX, atlasY, source.func_94275_d(), source.func_94276_e(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
    }

    @Override
    public void doTextureUpload(TextureStitched source)
    {
        // NO OP for copysubimage
    }

}
