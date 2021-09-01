/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.PickaxeItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiItemDecorationsEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RenderItemDecorationsEventTest.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderItemDecorationsEventTest {
    public static final boolean IS_ENABLED = false;
    public static final String MODID = "render_item_decorations_event_test";
    public static final ResourceLocation smiley = new ResourceLocation(MODID + ":textures/smiley.png");
    @SubscribeEvent
    public static void on_pre_render_items_decorations(final RenderGuiItemDecorationsEvent.Pre event)
    {
        if(event.getStack().getItem() instanceof PickaxeItem && IS_ENABLED)
        {
            //Pickaxes should no longer display their durability bar
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void on_post_render_items_decorations(final RenderGuiItemDecorationsEvent.Post event)
    {
        if(IS_ENABLED)
        {
            //Should display little smiley faces at the top left corner of every item
            RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();
            int x = event.getXOffset();
            int y = event.getYOffset();
            RenderSystem.setShaderTexture(0, smiley);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            bufferbuilder.vertex((double) (x + 0), (double) (y + 0), 0.0D).color(1.0f, 1.0f, 1.0f, 1.0f).uv(0, 0).endVertex();
            bufferbuilder.vertex((double) (x + 0), (double) (y + 5), 0.0D).color(1.0f, 1.0f, 1.0f, 1.0f).uv(0, 1).endVertex();
            bufferbuilder.vertex((double) (x + 5), (double) (y + 5), 0.0D).color(1.0f, 1.0f, 1.0f, 1.0f).uv(1, 1).endVertex();
            bufferbuilder.vertex((double) (x + 5), (double) (y + 0), 0.0D).color(1.0f, 1.0f, 1.0f, 1.0f).uv(1, 0).endVertex();
            bufferbuilder.end();
            BufferUploader.end(bufferbuilder);
        }
    }
    @SubscribeEvent
    public static void register_textures(final TextureStitchEvent.Pre event)
    {
        event.addSprite(smiley);
    }
}
