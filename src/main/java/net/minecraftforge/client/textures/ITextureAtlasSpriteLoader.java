package net.minecraftforge.client.textures;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;

@FunctionalInterface
public interface ITextureAtlasSpriteLoader
{

    TextureAtlasSprite load(
            AtlasTexture atlas,
            IResourceManager resourceManager, TextureAtlasSprite.Info textureInfo,
            IResource resource,
            int atlasWidth, int atlasHeight,
            int spriteX, int spriteY, int mipmapLevel,
            NativeImage image
    );

}
