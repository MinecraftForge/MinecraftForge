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

package net.minecraftforge.client.model.animation;

import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.render.BatchedBufferConfig;
import net.minecraftforge.client.render.BlockBufferDrawer;
import net.minecraftforge.client.render.IBufferDrawer;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;

import java.util.Collections;


public abstract class FastTESR<T extends TileEntity> extends TileEntitySpecialRenderer<T>
{
    private final BatchedBufferConfig config;
    @Deprecated
    public FastTESR()
    {
        this(BatchedBufferConfig.BLOCK);
    }

    public FastTESR(BatchedBufferConfig config)
    {
        this.config = config;
    }

    @Override
    public final void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float partial)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        config.drawer.preDraw(MinecraftForgeClient.getRenderPass(), buffer);
        buffer.begin(config.glMode, config.vertexFormat);

        renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, partial, Collections.singletonMap(this.config, tessellator));
        buffer.setTranslation(0, 0, 0);

        tessellator.draw();

        config.drawer.postDraw(MinecraftForgeClient.getRenderPass());
    }
}
