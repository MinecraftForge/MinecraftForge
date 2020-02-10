/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.function.Predicate;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL31;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.entity.Entity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

/* TODO: reimplement using the new rendering system? or remove?
public class CloudRenderer implements ISelectiveResourceReloadListener
{
    // Shared constants.
    private static final float PX_SIZE = 1 / 256F;

    // Building constants.
    private static final VertexFormat FORMAT = DefaultVertexFormats.POSITION_TEX_COLOR;
    private static final int TOP_SECTIONS = 12;    // Number of slices a top face will span.
    private static final int HEIGHT = 4;
    private static final float INSET = 0.001F;
    private static final float ALPHA = 0.8F;

    // Debug
    private static final boolean WIREFRAME = false;

    // Instance fields
    private final Minecraft mc = Minecraft.getInstance();
    private final ResourceLocation texture = new ResourceLocation("textures/environment/clouds.png");

    private VertexBuffer vbo;
    private CloudOption cloudMode = CloudOption.OFF;
    private int renderDistance = -1;

    private DynamicTexture COLOR_TEX = null;

    private int texW;
    private int texH;

    public CloudRenderer()
    {
        // Resource manager should always be reloadable.
        ((IReloadableResourceManager) mc.getResourceManager()).addReloadListener(this);
    }

    private int getScale()
    {
        return cloudMode == CloudOption.FANCY ? 12 : 8;
    }

    private float ceilToScale(float value)
    {
        float scale = getScale();
        return MathHelper.ceil(value / scale) * scale;
    }

    private void vertices(BufferBuilder buffer)
    {
        boolean fancy = cloudMode == CloudOption.FANCY;    // Defines whether to hide all but the bottom.

        float scale = getScale();
        float CULL_DIST = 2 * scale;

        float bCol = fancy ? 0.7F : 1F;

        float sectEnd = ceilToScale((renderDistance * 2) * 16);
        float sectStart = -sectEnd;

        float sectStep = ceilToScale(sectEnd * 2 / TOP_SECTIONS);
        float sectPx = PX_SIZE / scale;

        buffer.begin(GL11.GL_QUADS, FORMAT);

        float sectX0 = sectStart;
        float sectX1 = sectX0;

        while (sectX1 < sectEnd)
        {
            sectX1 += sectStep;

            if (sectX1 > sectEnd)
                sectX1 = sectEnd;

            float sectZ0 = sectStart;
            float sectZ1 = sectZ0;

            while (sectZ1 < sectEnd)
            {
                sectZ1 += sectStep;

                if (sectZ1 > sectEnd)
                    sectZ1 = sectEnd;

                float u0 = sectX0 * sectPx;
                float u1 = sectX1 * sectPx;
                float v0 = sectZ0 * sectPx;
                float v1 = sectZ1 * sectPx;

                // Bottom
                buffer.func_225582_a_(sectX0, 0, sectZ0).func_225583_a_(u0, v0).func_227885_a_(bCol, bCol, bCol, ALPHA).endVertex();
                buffer.func_225582_a_(sectX1, 0, sectZ0).func_225583_a_(u1, v0).func_227885_a_(bCol, bCol, bCol, ALPHA).endVertex();
                buffer.func_225582_a_(sectX1, 0, sectZ1).func_225583_a_(u1, v1).func_227885_a_(bCol, bCol, bCol, ALPHA).endVertex();
                buffer.func_225582_a_(sectX0, 0, sectZ1).func_225583_a_(u0, v1).func_227885_a_(bCol, bCol, bCol, ALPHA).endVertex();

                if (fancy)
                {
                    // Top
                    buffer.func_225582_a_(sectX0, HEIGHT, sectZ0).func_225583_a_(u0, v0).func_227885_a_(1, 1, 1, ALPHA).endVertex();
                    buffer.func_225582_a_(sectX0, HEIGHT, sectZ1).func_225583_a_(u0, v1).func_227885_a_(1, 1, 1, ALPHA).endVertex();
                    buffer.func_225582_a_(sectX1, HEIGHT, sectZ1).func_225583_a_(u1, v1).func_227885_a_(1, 1, 1, ALPHA).endVertex();
                    buffer.func_225582_a_(sectX1, HEIGHT, sectZ0).func_225583_a_(u1, v0).func_227885_a_(1, 1, 1, ALPHA).endVertex();

                    float slice;
                    float sliceCoord0;
                    float sliceCoord1;

                    for (slice = sectX0; slice < sectX1;)
                    {
                        sliceCoord0 = slice * sectPx;
                        sliceCoord1 = sliceCoord0 + PX_SIZE;

                        // X sides
                        if (slice > -CULL_DIST)
                        {
                            slice += INSET;
                            buffer.func_225582_a_(slice, 0,      sectZ1).func_225583_a_(sliceCoord0, v1).func_227885_a_(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.func_225582_a_(slice, HEIGHT, sectZ1).func_225583_a_(sliceCoord1, v1).func_227885_a_(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.func_225582_a_(slice, HEIGHT, sectZ0).func_225583_a_(sliceCoord1, v0).func_227885_a_(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.func_225582_a_(slice, 0,      sectZ0).func_225583_a_(sliceCoord0, v0).func_227885_a_(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            slice -= INSET;
                        }

                        slice += scale;

                        if (slice <= CULL_DIST)
                        {
                            slice -= INSET;
                            buffer.func_225582_a_(slice, 0,      sectZ0).func_225583_a_(sliceCoord0, v0).func_227885_a_(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.func_225582_a_(slice, HEIGHT, sectZ0).func_225583_a_(sliceCoord1, v0).func_227885_a_(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.func_225582_a_(slice, HEIGHT, sectZ1).func_225583_a_(sliceCoord1, v1).func_227885_a_(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.func_225582_a_(slice, 0,      sectZ1).func_225583_a_(sliceCoord0, v1).func_227885_a_(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            slice += INSET;
                        }
                    }

                    for (slice = sectZ0; slice < sectZ1;)
                    {
                        sliceCoord0 = slice * sectPx;
                        sliceCoord1 = sliceCoord0 + PX_SIZE;

                        // Z sides
                        if (slice > -CULL_DIST)
                        {
                            slice += INSET;
                            buffer.func_225582_a_(sectX0, 0,      slice).func_225583_a_(u0, sliceCoord0).func_227885_a_(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.func_225582_a_(sectX0, HEIGHT, slice).func_225583_a_(u0, sliceCoord1).func_227885_a_(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.func_225582_a_(sectX1, HEIGHT, slice).func_225583_a_(u1, sliceCoord1).func_227885_a_(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.func_225582_a_(sectX1, 0,      slice).func_225583_a_(u1, sliceCoord0).func_227885_a_(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            slice -= INSET;
                        }

                        slice += scale;

                        if (slice <= CULL_DIST)
                        {
                            slice -= INSET;
                            buffer.func_225582_a_(sectX1, 0,      slice).func_225583_a_(u1, sliceCoord0).func_227885_a_(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.func_225582_a_(sectX1, HEIGHT, slice).func_225583_a_(u1, sliceCoord1).func_227885_a_(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.func_225582_a_(sectX0, HEIGHT, slice).func_225583_a_(u0, sliceCoord1).func_227885_a_(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.func_225582_a_(sectX0, 0,      slice).func_225583_a_(u0, sliceCoord0).func_227885_a_(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            slice += INSET;
                        }
                    }
                }

                sectZ0 = sectZ1;
            }

            sectX0 = sectX1;
        }
    }

    private void dispose()
    {
        if (vbo != null)
        {
            vbo.deleteGlBuffers();
            vbo = null;
        }
    }

    private void build()
    {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        vbo = new VertexBuffer(FORMAT);

        vertices(buffer);

        buffer.finishDrawing();
        buffer.reset();
        vbo.bufferData(buffer.getByteBuffer());
    }

    private int fullCoord(double coord, int scale)
    {   // Corrects misalignment of UV offset when on negative coords.
        return ((int) coord / scale) - (coord < 0 ? 1 : 0);
    }

    private boolean isBuilt()
    {
        return vbo != null;
    }

    public void checkSettings()
    {
        boolean newEnabled = ForgeConfig.CLIENT.forgeCloudsEnabled.get()
                && mc.gameSettings.getCloudOption() != CloudOption.OFF
                && mc.world != null
                && mc.world.dimension.isSurfaceWorld();

        if (isBuilt()
                    && (!newEnabled
                    || mc.gameSettings.getCloudOption() != cloudMode
                    || mc.gameSettings.renderDistanceChunks != renderDistance))
        {
            dispose();
        }

        cloudMode = mc.gameSettings.getCloudOption();
        renderDistance = mc.gameSettings.renderDistanceChunks;

        if (newEnabled && !isBuilt())
        {
            build();
        }
    }

    public boolean render(int cloudTicks, float partialTicks)
    {
        if (!isBuilt())
            return false;

        Entity entity = mc.getRenderViewEntity();

        double totalOffset = cloudTicks + partialTicks;

        double x = entity.prevPosX + (entity.func_226277_ct_() - entity.prevPosX) * partialTicks
                + totalOffset * 0.03;
        double y = mc.world.dimension.getCloudHeight()
                - (entity.lastTickPosY + (entity.func_226278_cu_() - entity.lastTickPosY) * partialTicks)
                + 0.33;
        double z = entity.prevPosZ + (entity.func_226281_cx_() - entity.prevPosZ) * partialTicks;

        int scale = getScale();

        if (cloudMode == CloudOption.FANCY)
            z += 0.33 * scale;

        // Integer UVs to translate the texture matrix by.
        int offU = fullCoord(x, scale);
        int offV = fullCoord(z, scale);

        RenderSystem.pushMatrix();

        // Translate by the remainder after the UV offset.
        RenderSystem.translated((offU * scale) - x, y, (offV * scale) - z);

        // Modulo to prevent texture samples becoming inaccurate at extreme offsets.
        offU = offU % texW;
        offV = offV % texH;

        // Translate the texture.
        RenderSystem.matrixMode(GL11.GL_TEXTURE);
        RenderSystem.translatef(offU * PX_SIZE, offV * PX_SIZE, 0);
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);

        RenderSystem.disableCull();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Color multiplier.
        Vec3d color = mc.world.getCloudColour(partialTicks);
        float r = (float) color.x;
        float g = (float) color.y;
        float b = (float) color.z;

        if (COLOR_TEX == null)
            COLOR_TEX = new DynamicTexture(1, 1, false);

        // Apply a color multiplier through a texture upload if shaders aren't supported.
        COLOR_TEX.getTextureData().setPixelRGBA(0, 0, 255 << 24
                | ((int) (r * 255)) << 16
                | ((int) (g * 255)) << 8
                | (int) (b * 255));
        COLOR_TEX.updateDynamicTexture();

        RenderSystem.activeTexture(GL13.GL_TEXTURE1);
        RenderSystem.bindTexture(COLOR_TEX.getGlTextureId());
        RenderSystem.enableTexture();

        // Bind the clouds texture last so the shader's sampler2D is correct.
        RenderSystem.activeTexture(GL13.GL_TEXTURE0);
        mc.textureManager.bindTexture(texture);

        ByteBuffer buffer = Tessellator.getInstance().getBuffer().getByteBuffer();

        // Set up pointers for the display list/VBO.
        vbo.bindBuffer();

        int stride = FORMAT.getSize();
        RenderSystem.vertexPointer(3, GL11.GL_FLOAT, stride, 0);
        RenderSystem.enableClientState(GL11.GL_VERTEX_ARRAY);
        RenderSystem.texCoordPointer(2, GL11.GL_FLOAT, stride, 12);
        RenderSystem.enableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        RenderSystem.colorPointer(4, GL11.GL_UNSIGNED_BYTE, stride, 20);
        RenderSystem.enableClientState(GL11.GL_COLOR_ARRAY);

        // Depth pass to prevent insides rendering from the outside.
        RenderSystem.colorMask(false, false, false, false);
        vbo.drawArrays(GL11.GL_QUADS);

        // Full render.
        RenderSystem.colorMask(true, true, true, true);

        // Wireframe for debug.
        if (WIREFRAME)
        {
            RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            RenderSystem.lineWidth(2.0F);
            RenderSystem.disableTexture();
            RenderSystem.depthMask(false);
            RenderSystem.disableFog();
            vbo.drawArrays(GL11.GL_QUADS);
            RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();
            RenderSystem.enableFog();
        }

        vbo.drawArrays(GL11.GL_QUADS);
        VertexBuffer.unbindBuffer(); // Unbind buffer and disable pointers.

        buffer.limit(0);
        for (int i = 0; i < FORMAT.func_227894_c_().size(); i++)
            FORMAT.getElements().get(i).getUsage().postDraw(FORMAT, i, FORMAT.getSize(), buffer);
        buffer.position(0);

        // Disable our coloring.
        RenderSystem.activeTexture(GL13.GL_TEXTURE1);
        RenderSystem.disableTexture();
        RenderSystem.activeTexture(GL13.GL_TEXTURE0);

        // Reset texture matrix.
        RenderSystem.matrixMode(GL11.GL_TEXTURE);
        RenderSystem.loadIdentity();
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);

        RenderSystem.disableBlend();
        RenderSystem.enableCull();

        RenderSystem.popMatrix();

        return true;
    }

    private void reloadTextures()
    {
        if (mc.textureManager != null)
        {
            mc.textureManager.bindTexture(texture);
            texW = GlStateManager.func_227692_c_(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            texH = GlStateManager.func_227692_c_(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        }
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, @Nonnull Predicate<IResourceType> resourcePredicate)
    {
        if (resourcePredicate.test(VanillaResourceType.TEXTURES))
        {
            reloadTextures();
        }
    }

    private static CloudRenderer cloudRenderer;
    private static CloudRenderer getCloudRenderer()
    {
        if (cloudRenderer == null)
            cloudRenderer = new CloudRenderer();
        return cloudRenderer;
    }

    public static void updateCloudSettings()
    {
        getCloudRenderer().checkSettings();
    }

    public static boolean renderClouds(int cloudTicks, float partialTicks, ClientWorld world, Minecraft client)
    {
        IRenderHandler renderer = world.dimension.getCloudRenderer();
        if (renderer != null)
        {
            renderer.render(cloudTicks, partialTicks, world, client);
            return true;
        }
        return getCloudRenderer().render(cloudTicks, partialTicks);
    }

}
*/