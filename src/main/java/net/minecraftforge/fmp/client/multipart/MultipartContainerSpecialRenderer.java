package net.minecraftforge.fmp.client.multipart;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.fmp.block.TileCoverable;
import net.minecraftforge.fmp.block.TileMultipartContainer;
import net.minecraftforge.fmp.multipart.IMultipart;
import net.minecraftforge.fmp.multipart.IMultipartContainer;
import net.minecraftforge.fmp.multipart.MultipartRegistry;

public final class MultipartContainerSpecialRenderer
{

    private static final EnumFacing[] ALL_ENUM_FACING = new EnumFacing[]
        { EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, null };

    public static boolean renderMultipartContainerAt(IMultipartContainer container, double x, double y, double z, float partialTicks,
            int destroyStage, TileEntityRendererDispatcher rendererDispatcher)
    {
        if (destroyStage >= 0)
        {
            IVertexConsumer consumer = new VertexBufferConsumer(Tessellator.getInstance().getBuffer());
            startBreaking(rendererDispatcher);

            RayTraceResult mop = Minecraft.getMinecraft().objectMouseOver;
            if (mop != null && mop.typeOfHit == Type.BLOCK && mop.getBlockPos() != null && mop.getBlockPos().equals(container.getPosIn())
                    && mop.partHit == null)
            {
                renderBreaking(((RayTraceResult) mop).partHit, consumer, x, y, z, partialTicks, destroyStage, rendererDispatcher);
            }
            else
            {
                for (IMultipart part : container.getParts())
                {
                    renderBreaking(part, consumer, x, y, z, partialTicks, destroyStage, rendererDispatcher);
                }
            }

            finishBreaking();
            return true;
        }

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        rendererDispatcher.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (Minecraft.isAmbientOcclusionEnabled())
        {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        }
        else
        {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        for (IMultipart part : container.getParts())
        {
            MultipartSpecialRenderer<IMultipart> renderer = MultipartRegistryClient.getSpecialRenderer(part);
            if (renderer != null && renderer.shouldRenderInPass(part, MinecraftForgeClient.getRenderPass()) && part.hasFastRenderer())
            {
                renderer.setRendererDispatcher(rendererDispatcher);
                renderer.renderMultipartFast(part, x, y, z, partialTicks, destroyStage, buffer);
            }
        }
        buffer.setTranslation(0, 0, 0);
        tessellator.draw();
        RenderHelper.enableStandardItemLighting();

        for (IMultipart part : container.getParts())
        {
            MultipartSpecialRenderer<IMultipart> renderer = MultipartRegistryClient.getSpecialRenderer(part);
            if (renderer != null && renderer.shouldRenderInPass(part, MinecraftForgeClient.getRenderPass()) && !part.hasFastRenderer())
            {
                renderer.setRendererDispatcher(rendererDispatcher);
                renderer.renderMultipartAt(part, x, y, z, partialTicks, destroyStage);
            }
        }
        return false;
    }

    public static boolean renderMultipartContainerFast(IMultipartContainer container, double x, double y, double z, float partialTicks,
            int destroyStage, TileEntityRendererDispatcher rendererDispatcher, VertexBuffer buffer)
    {
        for (IMultipart part : container.getParts())
        {
            MultipartSpecialRenderer<IMultipart> renderer = MultipartRegistryClient.getSpecialRenderer(part);
            if (renderer != null && renderer.shouldRenderInPass(part, MinecraftForgeClient.getRenderPass()))
            {
                renderer.setRendererDispatcher(rendererDispatcher);
                renderer.renderMultipartFast(part, x, y, z, partialTicks, destroyStage, buffer);
            }
        }
        return false;
    }

    private static void startBreaking(TileEntityRendererDispatcher rendererDispatcher)
    {
        GlStateManager.pushMatrix();
        GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
        GlStateManager.doPolygonOffset(-3.0F, -3.0F);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableAlpha();
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
    }

    private static void startTessellating(double x, double y, double z)
    {
        Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        Tessellator.getInstance().getBuffer().setTranslation(x, y, z);
        Tessellator.getInstance().getBuffer().noColor();
    }

    private static void renderBreaking(IMultipart part, IVertexConsumer consumer, double x, double y, double z, float partialTicks,
            int destroyStage, TileEntityRendererDispatcher rendererDispatcher)
    {
        MultipartSpecialRenderer<IMultipart> renderer = MultipartRegistryClient.getSpecialRenderer(part);
        if (renderer != null && renderer.canRenderBreaking(part))
        {
            renderer.setRendererDispatcher(rendererDispatcher);
            renderer.renderMultipartAt(part, x, y, z, partialTicks, destroyStage);
        }
        else
        {
            if (MinecraftForgeClient.getRenderPass() == 1)
            {
                ResourceLocation path = part.getModelPath();
                IBlockState state = part.getExtendedState(MultipartRegistry.getDefaultState(part).getBaseState());
                IBakedModel model = path == null ? null
                        : Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getModel(
                                new ModelResourceLocation(path, MultipartStateMapper.instance.getPropertyString(state.getProperties())));
                if (model != null)
                {
                    for (BlockRenderLayer layer : BlockRenderLayer.values())
                    {
                        if (part.canRenderInLayer(layer))
                        {
                            ForgeHooksClient.setRenderLayer(layer);
                            IBakedModel layerModel = (new SimpleBakedModel.Builder(state, model,
                                    Minecraft.getMinecraft().getTextureMapBlocks()
                                            .getAtlasSprite("minecraft:blocks/destroy_stage_" + destroyStage),
                                    part.getPos())).makeBakedModel();
                            rendererDispatcher.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                            startTessellating(x, y, z);
                            consumer = new VertexBufferConsumer(Tessellator.getInstance().getBuffer());
                            renderBreaking(state, layerModel, consumer);
                            finishTessellating();
                        }
                    }
                    ForgeHooksClient.setRenderLayer(BlockRenderLayer.SOLID);
                }
            }
        }
    }

    private static void renderBreaking(IBlockState state, IBakedModel model, IVertexConsumer consumer)
    {
        for (EnumFacing side : ALL_ENUM_FACING)
        {
            for (BakedQuad quad : model.getQuads(state, side, 0L))
            {
                quad.pipe(consumer);
            }
        }
    }

    private static void finishTessellating()
    {
        Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);
        Tessellator.getInstance().draw();
    }

    private static void finishBreaking()
    {
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.disableAlpha();
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    public static final class TileMultipartSpecialRenderer extends TileEntitySpecialRenderer<TileMultipartContainer>
    {

        @Override
        public void renderTileEntityAt(TileMultipartContainer te, double x, double y, double z, float partialTicks, int destroyStage)
        {
            renderMultipartContainerAt(te.getPartContainer(), x, y, z, partialTicks, destroyStage, rendererDispatcher);
        }

        @Override
        public void renderTileEntityFast(TileMultipartContainer te, double x, double y, double z, float partialTicks, int destroyStage,
                VertexBuffer buffer)
        {
            renderMultipartContainerFast(te.getPartContainer(), x, y, z, partialTicks, destroyStage, rendererDispatcher, buffer);
        }

    }

    public static class TileCoverableSpecialRenderer<T extends TileCoverable> extends TileEntitySpecialRenderer<T>
    {

        @Override
        public void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage)
        {
            if (destroyStage >= 0)
            {
                if (MinecraftForgeClient.getRenderPass() != 1)
                {
                    return;
                }

                RayTraceResult mop = Minecraft.getMinecraft().objectMouseOver;
                if (mop != null && mop.typeOfHit == Type.BLOCK && mop.getBlockPos() != null && mop.getBlockPos().equals(te.getPosIn())
                        && mop.partHit == null)
                {
                    IVertexConsumer consumer = new VertexBufferConsumer(Tessellator.getInstance().getBuffer());
                    startBreaking(rendererDispatcher);
                    if (canRenderBreaking())
                    {
                        renderTileEntityAtDefault(te, x, y, z, partialTicks, destroyStage);
                    }
                    else
                    {
                        IBlockState state = te.getWorldIn().getBlockState(te.getPosIn());
                        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes()
                                .getModelForState(state.getActualState(te.getWorldIn(), te.getPosIn()));
                        if (model != null && model instanceof ModelMultipartContainer)
                        {
                            model = ((ModelMultipartContainer) model).model;
                        }
                        if (model != null)
                        {
                            model = (new SimpleBakedModel.Builder(state, model,
                                    Minecraft.getMinecraft().getTextureMapBlocks()
                                            .getAtlasSprite("minecraft:blocks/destroy_stage_" + destroyStage),
                                    te.getPosIn())).makeBakedModel();
                            startTessellating(x, y, z);
                            renderBreaking(state, model, consumer);
                            finishTessellating();
                        }
                    }

                    finishBreaking();
                    return;
                }
            }

            if (renderMultipartContainerAt(te.getMicroblockContainer(), x, y, z, partialTicks, destroyStage,
                    rendererDispatcher))
                return;
            renderTileEntityAtDefault(te, x, y, z, partialTicks, destroyStage);
        }

        public void renderTileEntityAtDefault(T te, double x, double y, double z, float partialTicks, int destroyStage)
        {
        }

        @Override
        public void renderTileEntityFast(T te, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer buffer)
        {
            if (renderMultipartContainerFast(te.getMicroblockContainer(), x, y, z, partialTicks, destroyStage, rendererDispatcher,
                    buffer))
                return;
            renderTileEntityFastDefault(te, x, y, z, partialTicks, destroyStage, buffer);
        }

        public void renderTileEntityFastDefault(T te, double x, double y, double z, float partialTicks, int destroyStage,
                VertexBuffer buffer)
        {
        }

        public boolean canRenderBreaking()
        {
            return false;
        }

    }

}
