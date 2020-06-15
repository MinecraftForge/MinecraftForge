package net.minecraftforge.debug.client.rendering;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldPreTranslucentEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * A simple mod to test the RenderWorldPreTranslucentEvent. For this test, a
 * diamond ore "block" is rendered at the coordinates (0, 128, 0).
 */

@Mod(RenderWorldPreTranslucentEventTest.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderWorldPreTranslucentEventTest
{
    public static final String MODID = "pre_translucent_render_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onWorldRenderPreTranslucent(RenderWorldPreTranslucentEvent event)
    {
        if(!ENABLED)
            return;

        // Get instances of the classes required for a block render.
        MinecraftServer server = Minecraft.getInstance().getIntegratedServer();
        World world = DimensionManager.getWorld(server, DimensionType.OVERWORLD, false, true);
        MatrixStack matrixStack = event.getMatrixStack();
        Impl renderTypeBuffers = event.getRenderTypeBuffers();

        // Get the projected view coordinates.
        @SuppressWarnings("resource")
        Vec3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();

        // Choose diamond ore as the arbitrary block.
        BlockState blockState = Blocks.DIAMOND_ORE.getDefaultState();

        // Render the block at the coordinates (0, 128, 0).
        renderBlock(matrixStack, renderTypeBuffers, world, blockState, new BlockPos(0, 128, 0), projectedView, new Vec3d(0.0, 128.0, 0.0));

        renderTypeBuffers.finish();
    }

    @SuppressWarnings("deprecation")
    public static void renderBlock(MatrixStack matrixStack, Impl renderTypeBuffers, World world, BlockState blockState, BlockPos logicPos, Vec3d projectedView, Vec3d renderCoordinates)
    {
        BlockRendererDispatcher blockRendererDispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        int i = OverlayTexture.NO_OVERLAY;

        matrixStack.push();
        matrixStack.translate(-projectedView.x + renderCoordinates.x, -projectedView.y + renderCoordinates.y, -projectedView.z + renderCoordinates.z);

        for(RenderType renderType : RenderType.getBlockRenderTypes())
        {
            if(RenderTypeLookup.canRenderInLayer(blockState, renderType))
                blockRendererDispatcher.getBlockModelRenderer().renderModel(world, blockRendererDispatcher.getModelForState(blockState), blockState, logicPos, matrixStack, renderTypeBuffers.getBuffer(renderType), true, new Random(), blockState.getPositionRandom(logicPos), i);
        }

        matrixStack.pop();
    }
}
