package net.minecraftforge.fmp.client.multipart;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fmp.multipart.IMultipart;

public abstract class MultipartSpecialRenderer<T extends IMultipart>
{
    
    protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]
        { new ResourceLocation("textures/blocks/destroy_stage_0.png"), new ResourceLocation("textures/blocks/destroy_stage_1.png"),
                new ResourceLocation("textures/blocks/destroy_stage_2.png"), new ResourceLocation("textures/blocks/destroy_stage_3.png"),
                new ResourceLocation("textures/blocks/destroy_stage_4.png"), new ResourceLocation("textures/blocks/destroy_stage_5.png"),
                new ResourceLocation("textures/blocks/destroy_stage_6.png"), new ResourceLocation("textures/blocks/destroy_stage_7.png"),
                new ResourceLocation("textures/blocks/destroy_stage_8.png"), new ResourceLocation("textures/blocks/destroy_stage_9.png") };
    protected TileEntityRendererDispatcher rendererDispatcher;

    public abstract void renderMultipartAt(T part, double x, double y, double z, float partialTicks, int destroyStage);

    public void renderMultipartFast(T part, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer buffer)
    {
    }

    public boolean shouldRenderInPass(T part, int pass)
    {
        return pass == 0;
    }

    public boolean canRenderBreaking(T part)
    {
        return false;
    }

    protected void bindTexture(ResourceLocation location)
    {
        TextureManager texturemanager = this.rendererDispatcher.renderEngine;
        if (texturemanager != null)
        {
            texturemanager.bindTexture(location);
        }
    }

    protected World getWorld()
    {
        return this.rendererDispatcher.worldObj;
    }

    public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcher)
    {
        this.rendererDispatcher = rendererDispatcher;
    }

    public FontRenderer getFontRenderer()
    {
        return this.rendererDispatcher.getFontRenderer();
    }

}
