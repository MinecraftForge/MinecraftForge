package net.minecraftforge.common.data;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class ForgeSpriteSourceProvider extends SpriteSourceProvider
{
    public ForgeSpriteSourceProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        super(generator, fileHelper, "forge");
    }

    @Override
    protected void addSources()
    {
        atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(new ResourceLocation("forge:white"), Optional.empty()));
    }
}
