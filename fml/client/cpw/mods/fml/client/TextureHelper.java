package cpw.mods.fml.client;

import java.nio.ByteBuffer;
import java.util.List;

import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureStitched;

public abstract class TextureHelper {

    /**
     * Copy the texture from the source to the atlas at the specified position
     *
     * This will use the devised GL helper to do either GL-side copy or a subimage upload
     *
     * @param atlas The atlas texture we're copying into
     * @param source The source texture we're copying from (complete)
     * @param atlasX The X position on the atlas
     * @param atlasY The Y position on the atlas
     */
    public abstract void doTextureCopy(Texture atlas, Texture source, int atlasX, int atlasY);

    /**
     * Upload the texture to the GPU for GL side copying operations
     * This may be a no-op depending on the active implementation.
     *
     * @param source The texture to upload
     */
    public abstract void doTextureUpload(TextureStitched source);

    /**
     * Rotate the texture so that it doesn't need a rotational transform applied each tick
     *
     * @param texture The texture to rotate
     * @param buffer The buffer for the texture
     */
    public void rotateTexture(Texture texture, ByteBuffer buffer)
    {
        ByteBuffer bytebuffer = buffer;
        buffer.position(0);
        ByteBuffer other = ByteBuffer.allocateDirect(buffer.capacity());
        other.position(0);

        for (int k = 0; k < texture.func_94276_e(); ++k)
        {
            int l = texture.func_94276_e() - k - 1;
            int i1 = k * texture.func_94275_d() * 4;

            for (int k1 = 0; k1 < texture.func_94275_d(); ++k1)
            {
                int l1 = k1 * texture.func_94276_e() * 4 + l * 4;
                int i2 = i1 + k1 * 4;

                other.put(l1 + 0, bytebuffer.get(i2 + 0));
                other.put(l1 + 1, bytebuffer.get(i2 + 1));
                other.put(l1 + 2, bytebuffer.get(i2 + 2));
                other.put(l1 + 3, bytebuffer.get(i2 + 3));
            }
        }
        buffer.position(0);
        buffer.put(other);
    }

}
