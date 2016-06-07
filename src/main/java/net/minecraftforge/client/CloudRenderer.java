package net.minecraftforge.client;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public enum CloudRenderer implements IResourceManagerReloadListener
{
    INSTANCE;

    // Shared constants.
    private static final float PX_SIZE = 1 / 256F;

    // Building constants.
    private static final VertexFormat FORMAT = DefaultVertexFormats.POSITION_TEX_COLOR;
    private static final int HEIGHT = 4;
    private static final int FULL_WIDTH = 64;
    private static final int START = -FULL_WIDTH / 2;
    private static final int END = FULL_WIDTH / 2;
    private static final int SECTION_WIDTH = 8;
    private static final float INSET = 0.001F;
    private static final float ALPHA = 0.8F;

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ResourceLocation texture = new ResourceLocation("textures/environment/clouds.png");

    private int ticks = 0;

    private int displayList = -1;
    private net.minecraft.client.renderer.vertex.VertexBuffer vbo;
    private boolean configEnabled = false;
    private int cloudMode = -1;

    private final DynamicTexture COLOR_TEX = new DynamicTexture(1, 1);

    private int texW;
    private int texH;

    private CloudRenderer()
    {
        MinecraftForge.EVENT_BUS.register(this);
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(this);
    }

    private int getScale()
    {
        return cloudMode == 2 ? 12 : 8;
    }

    private void vertices(VertexBuffer buffer)
    {
        boolean fancy = cloudMode == 2;    // Defines whether to hide all but the bottom.

        float scale = getScale();
        float cullDist = 2 * scale;

        float bCol = fancy ? 0.7F : 1F;

        buffer.begin(GL11.GL_QUADS, FORMAT);

        // Create 1200 quads (with SECTION_WIDTH 8). SECTION_WIDTH defines width of slices and vertical faces.
        // With the minimum number (built above), the clouds nearest the player will still be fully fogged.
        float sectStart = START * scale;
        float sectEnd = END * scale;
        float sectStep = SECTION_WIDTH * scale;
        float sectPx = PX_SIZE / scale;

        float sectX0 = sectStart;
        float sectX1;

        for (sectX1 = sectStart + sectStep; sectX1 <= sectEnd; sectX1 += sectStep)
        {
            if (Float.isNaN(sectX0))
            {
                sectX0 = sectX1;
                continue;
            }

            float sectZ0 = sectStart;
            float sectZ1;

            for (sectZ1 = sectStart + sectStep; sectZ1 <= sectEnd; sectZ1 += sectStep)
            {
                float u0 = sectX0 * sectPx;
                float u1 = sectX1 * sectPx;
                float v0 = sectZ0 * sectPx;
                float v1 = sectZ1 * sectPx;

                // Bottom
                buffer.pos(sectX0, 0, sectZ0).tex(u0, v0).color(bCol, bCol, bCol, ALPHA).endVertex();
                buffer.pos(sectX1, 0, sectZ0).tex(u1, v0).color(bCol, bCol, bCol, ALPHA).endVertex();
                buffer.pos(sectX1, 0, sectZ1).tex(u1, v1).color(bCol, bCol, bCol, ALPHA).endVertex();
                buffer.pos(sectX0, 0, sectZ1).tex(u0, v1).color(bCol, bCol, bCol, ALPHA).endVertex();

                if (fancy)
                {
                    // Top
                    buffer.pos(sectX0, HEIGHT, sectZ0).tex(u0, v0).color(1, 1, 1, ALPHA).endVertex();
                    buffer.pos(sectX0, HEIGHT, sectZ1).tex(u0, v1).color(1, 1, 1, ALPHA).endVertex();
                    buffer.pos(sectX1, HEIGHT, sectZ1).tex(u1, v1).color(1, 1, 1, ALPHA).endVertex();
                    buffer.pos(sectX1, HEIGHT, sectZ0).tex(u1, v0).color(1, 1, 1, ALPHA).endVertex();

                    float slice;
                    float sliceCoord0;
                    float sliceCoord1;

                    for (slice = sectX0; slice < sectX1;)
                    {
                        sliceCoord0 = slice * sectPx;
                        sliceCoord1 = sliceCoord0 + PX_SIZE;

                        // X sides
                        if (slice > -cullDist)
                        {
                            slice += INSET;
                            buffer.pos(slice, 0,      sectZ1).tex(sliceCoord0, v1).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, HEIGHT, sectZ1).tex(sliceCoord1, v1).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, HEIGHT, sectZ0).tex(sliceCoord1, v0).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, 0,      sectZ0).tex(sliceCoord0, v0).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            slice -= INSET;
                        }

                        slice += scale;

                        if (slice <= cullDist)
                        {
                            slice -= INSET;
                            buffer.pos(slice, 0,      sectZ0).tex(sliceCoord0, v0).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, HEIGHT, sectZ0).tex(sliceCoord1, v0).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, HEIGHT, sectZ1).tex(sliceCoord1, v1).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, 0,      sectZ1).tex(sliceCoord0, v1).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            slice += INSET;
                        }
                    }

                    for (slice = sectZ0; slice < sectZ1;)
                    {
                        sliceCoord0 = slice * sectPx;
                        sliceCoord1 = sliceCoord0 + PX_SIZE;

                        // Z sides
                        if (slice > -cullDist)
                        {
                            slice += INSET;
                            buffer.pos(sectX0, 0,      slice).tex(u0, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX0, HEIGHT, slice).tex(u0, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX1, HEIGHT, slice).tex(u1, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX1, 0,      slice).tex(u1, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            slice -= INSET;
                        }

                        slice += scale;

                        if (slice <= cullDist)
                        {
                            slice -= INSET;
                            buffer.pos(sectX1, 0,      slice).tex(u1, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX1, HEIGHT, slice).tex(u1, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX0, HEIGHT, slice).tex(u0, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX0, 0,      slice).tex(u0, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            slice += INSET;
                        }
                    }
                }

                sectZ0 = sectZ1;
            }

            sectX0 = sectX1;
        }
    }

    private void rebuild()
    {
        if (vbo != null)
            vbo.deleteGlBuffers();
        if (displayList >= 0)
        {
            GLAllocation.deleteDisplayLists(displayList);
            displayList = -1;
        }

        if (configEnabled && mc.gameSettings.shouldRenderClouds() != 0)
        {
            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buffer = tess.getBuffer();

            if (OpenGlHelper.useVbo())
                vbo = new net.minecraft.client.renderer.vertex.VertexBuffer(FORMAT);
            else
                GlStateManager.glNewList(displayList = GLAllocation.generateDisplayLists(1), GL11.GL_COMPILE);

            vertices(buffer);

            if (OpenGlHelper.useVbo())
            {
                buffer.finishDrawing();
                buffer.reset();
                vbo.bufferData(buffer.getByteBuffer());
            }
            else
            {
                tess.draw();
                GlStateManager.glEndList();
            }
        }
    }

    private int fullCoord(double coord, int scale)
    {   // Corrects misalignment of UV offset when on negative coords.
        return ((int) coord / scale) - (coord < 0 ? 1 : 0);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && !mc.isGamePaused())
            ticks++;
    }

    public boolean render(float partialTicks)
    {
        if (!mc.theWorld.provider.isSurfaceWorld())
            return true;

        if (ForgeModContainer.forgeCloudsEnabled != configEnabled
                || cloudMode != mc.gameSettings.shouldRenderClouds()
                || (OpenGlHelper.useVbo() ? vbo == null : displayList < 0))
        {
            configEnabled = ForgeModContainer.forgeCloudsEnabled;
            cloudMode = mc.gameSettings.shouldRenderClouds();
            rebuild();
        }

        if (!configEnabled)
            return false;

        Entity entity = mc.getRenderViewEntity();

        double totalOffset = ticks + partialTicks;

        double x = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks
                + totalOffset * 0.03;
        double y = mc.theWorld.provider.getCloudHeight()
                - (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks)
                + 0.33;
        double z = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;

        if (cloudMode == 2)
            z += 0.33 * getScale();

        int scale = getScale();

        // Integer UVs to translate the texture matrix by.
        int offU = fullCoord(x, scale);
        int offV = fullCoord(z, scale);

        GlStateManager.pushMatrix();

        // Translate by the remainder after the UV offset.
        GlStateManager.translate((offU * scale) - x, y, (offV * scale) - z);

        // Modulo to prevent texture samples becoming inaccurate at extreme offsets.
        offU = Math.floorMod(offU, texW);
        offV = Math.floorMod(offV, texH);

        // Translate the texture.
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.translate(offU * PX_SIZE, offV * PX_SIZE, 0);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        GlStateManager.disableCull();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        // Color multiplier.
        Vec3d color = mc.theWorld.getCloudColour(partialTicks);
        float r = (float) color.xCoord;
        float g = (float) color.yCoord;
        float b = (float) color.zCoord;

        if (mc.gameSettings.anaglyph)
        {
            float tempR = r * 0.3F + g * 0.59F + b * 0.11F;
            float tempG = r * 0.3F + g * 0.7F;
            float tempB = r * 0.3F + b * 0.7F;
            r = tempR;
            g = tempG;
            b = tempB;
        }

        // Apply a color multiplier through a texture upload if shaders aren't supported.
        COLOR_TEX.getTextureData()[0] = 255 << 24
                | ((int) (r * 255)) << 16
                | ((int) (g * 255)) << 8
                | (int) (b * 255);
        COLOR_TEX.updateDynamicTexture();

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.bindTexture(COLOR_TEX.getGlTextureId());
        GlStateManager.enableTexture2D();

        // Bind the clouds texture last so the shader's sampler2D is correct.
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        mc.renderEngine.bindTexture(texture);

        ByteBuffer buffer = Tessellator.getInstance().getBuffer().getByteBuffer();

        // Set up pointers for the display list/VBO.
        if (OpenGlHelper.useVbo())
        {
            vbo.bindBuffer();

            int stride = FORMAT.getNextOffset();
            GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, stride, 0);
            GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, stride, 12);
            GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, stride, 20);
            GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
        }
        else
        {
            buffer.limit(FORMAT.getNextOffset());
            for (int i = 0; i < FORMAT.getElementCount(); i++)
                FORMAT.getElements().get(i).getUsage().preDraw(FORMAT, i, FORMAT.getNextOffset(), buffer);
            buffer.position(0);
        }

        // Depth pass to prevent insides rendering from the outside.
        GlStateManager.colorMask(false, false, false, false);
        if (OpenGlHelper.useVbo())
            vbo.drawArrays(GL11.GL_QUADS);
        else
            GlStateManager.callList(displayList);

        // Full render.
        if (!mc.gameSettings.anaglyph)
        {
            GlStateManager.colorMask(true, true, true, true);
        }
        else
        {
            switch (EntityRenderer.anaglyphField)
            {
            case 0:
                GlStateManager.colorMask(false, true, true, true);
                break;
            case 1:
                GlStateManager.colorMask(true, false, false, true);
                break;
            }
        }
        if (OpenGlHelper.useVbo())
            vbo.drawArrays(GL11.GL_QUADS);
        else
            GlStateManager.callList(displayList);

        // Unbind buffer and disable pointers.
        if (OpenGlHelper.useVbo())
            vbo.unbindBuffer();

        buffer.limit(0);
        for (int i = 0; i < FORMAT.getElementCount(); i++)
            FORMAT.getElements().get(i).getUsage().postDraw(FORMAT, i, FORMAT.getNextOffset(), buffer);
        buffer.position(0);

        // Disable our coloring.
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        // Reset texture matrix.
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        GlStateManager.disableBlend();
        GlStateManager.enableCull();

        GlStateManager.popMatrix();

        return true;
    }

    private void reloadTextures()
    {
        mc.renderEngine.bindTexture(texture);
        texW = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        texH = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        reloadTextures();
    }
}
