package net.minecraftforge.client.textures;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;

@FunctionalInterface
public interface ITextureAtlasSpriteLoader
{

    TextureAtlasSprite load(
            IResourceManager resourceManager, TextureAtlasSprite.Info textureInfo,
            IResource resource,
            int atlasWidth, int atlasHeight,
            int spriteX, int spriteY, int mipmapLevel
    );

}
