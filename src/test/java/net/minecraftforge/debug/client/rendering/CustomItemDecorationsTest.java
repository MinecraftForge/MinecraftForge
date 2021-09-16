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
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.client.ItemDecorator;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemDecoratorHandler;
import net.minecraftforge.items.ItemDecoratorHandler;
import net.minecraftforge.items.IItemDecoratorHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(CustomItemDecorationsTest.MODID)
public class CustomItemDecorationsTest {
    public static final String MODID = "render_item_decorations_test";
    private static final boolean IS_ENABLED = true;
    private static final ResourceLocation smiley = new ResourceLocation(MODID + ":textures/smiley.png");
    private static ItemDecorator decorator = new ItemDecorator(new ResourceLocation(MODID, "test_decorator")) {
        @Override
        public void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String stackSizeLabel) {
            RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();
            int x = xOffset;
            int y = yOffset;
            RenderSystem.setShaderTexture(0, smiley);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            bufferbuilder.vertex(x + 0, y + 0, 0.0D).color(1.0f, 1.0f, 1.0f, 1.0f).uv(0, 0).endVertex();
            bufferbuilder.vertex(x + 0, y + 5, 0.0D).color(1.0f, 1.0f, 1.0f, 1.0f).uv(0, 1).endVertex();
            bufferbuilder.vertex(x + 5, y + 5, 0.0D).color(1.0f, 1.0f, 1.0f, 1.0f).uv(1, 1).endVertex();
            bufferbuilder.vertex(x + 5, y + 0, 0.0D).color(1.0f, 1.0f, 1.0f, 1.0f).uv(1, 0).endVertex();
            bufferbuilder.end();
            BufferUploader.end(bufferbuilder);
        }
    };

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Capabilities
    {
        @SubscribeEvent
        public static void onAttachCapabilities(final AttachCapabilitiesEvent<ItemStack> event) {
            if (IS_ENABLED) {
                if (!(event.getObject().getItem() instanceof DiggerItem)) return;
                ItemDecoratorHandler handler = new ItemDecoratorHandler();
                handler.addDecorator(decorator);
                LazyOptional<IItemDecoratorHandler> optional = LazyOptional.of(() -> handler);
                ICapabilityProvider provider = new ICapabilityProvider() {
                    @Nonnull
                    @Override
                    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                        if (cap == CapabilityItemDecoratorHandler.ITEM_DECORATOR_HANDLER_CAPABILITY) {
                            return optional.cast();
                        }
                        return LazyOptional.empty();
                    }
                };
                event.addCapability(new ResourceLocation(MODID, "item_decorator_test"), provider);
                event.addListener(optional::invalidate);
            }
        }
    }
}
