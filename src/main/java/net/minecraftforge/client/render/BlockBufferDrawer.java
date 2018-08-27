/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class BlockBufferDrawer implements IBufferDrawer
{
    public static final BlockBufferDrawer INSTANCE = new BlockBufferDrawer();

    private BlockBufferDrawer() {}

    @Override
    public void preDraw(int renderPass, BufferBuilder builder)
    {
        TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
        renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (net.minecraft.client.Minecraft.isAmbientOcclusionEnabled())
        {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        }
        else
        {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        if(renderPass > 0)
        {
            Vec3d cameraPos = ActiveRenderInfo.getCameraPosition();
            builder.sortVertexData((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
        }
    }

    @Override
    public void postDraw(int renderPass)
    {
        RenderHelper.enableStandardItemLighting();
    }
}
