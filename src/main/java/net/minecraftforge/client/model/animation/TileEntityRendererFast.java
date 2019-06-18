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

package net.minecraftforge.client.model.animation;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.tileentity.TileEntity;

/**
 * A special case {@link TileGameRenderer} which can be batched with other
 * renderers that are also instances of this class.
 * <p>
 * Advantages:
 * <ul>
 * <li>All batched renderers are drawn with a single draw call</li>
 * <li>Renderers have their vertices depth sorted for better translucency
 * support</li>
 * </ul>
 * <p>
 * Disadvantages:
 * <ul>
 * <li>OpenGL operations are not permitted</li>
 * <li>All renderers must use the same {@link VertexFormat}
 * ({@link DefaultVertexFormats#BLOCK})</li>
 * </ul>
 *
 * @param <T> The type of {@link TileEntity} being rendered.
 */
public abstract class TileEntityRendererFast<T extends TileEntity> extends TileEntityRenderer<T>
{
    @Override
    public final void render(T te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
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

        renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, buffer);
        buffer.setTranslation(0, 0, 0);

        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
    }

    /**
     * Draw this renderer to the passed {@link BufferBuilder}. <strong>DO
     * NOT</strong> draw to any buffers other than the one passed, or use any OpenGL
     * operations as they will not be applied when this renderer is batched.
     */
    @Override
    public abstract void renderTileEntityFast(T te, double x, double y, double z, float partialTicks, int destroyStage, BufferBuilder buffer);
}
