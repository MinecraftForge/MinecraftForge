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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.CustomItemDecorator;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.items.CapabilityCustomItemDecoration;
import net.minecraftforge.items.CustomItemDecorationHandler;
import net.minecraftforge.items.ICustomItemDecoration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(CustomItemDecorationsTest.MODID)
@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD)
public class CustomItemDecorationsTest
{
    public static final String MODID = "render_item_decorations_test";
    public static final boolean IS_ENABLED = true;
    public static final ResourceLocation smiley = new ResourceLocation(MODID + ":textures/smiley.png");
    /*
    @SubscribeEvent
    public static void register_textures(final TextureStitchEvent.Pre event)
    {
        event.addSprite(smiley);
    }*/
    
    @SubscribeEvent
    public static void onAttachCapabilities(final AttachCapabilitiesEvent<ItemStack> event)
    {
        if(!(event.getObject().getItem() instanceof DiggerItem)) return;
        CustomItemDecorationHandler handler = new CustomItemDecorationHandler();
        handler.addDecoration(new ResourceLocation(MODID, "test"), event.getObject());
        LazyOptional<ICustomItemDecoration> optional = LazyOptional.of(() -> handler);
        ICapabilityProvider provider = new ICapabilitySerializable<CompoundTag>() {
            @Override
            public CompoundTag serializeNBT() {
                return handler.serializeNBT();
            }
    
            @Override
            public void deserializeNBT(CompoundTag nbt) {
                handler.deserializeNBT(nbt);
            }
    
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if(cap == CapabilityCustomItemDecoration.CUSTOM_ITEM_DECORATION_CAPABILITY) {
                    return optional.cast();
                }
                return LazyOptional.empty();
            }
        };
        event.addCapability(new ResourceLocation(MODID, "item_renderer_test"), provider);
        event.addListener(optional::invalidate);
    }
    
    @Mod.EventBusSubscriber(value= Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientOnly
    {
        @SubscribeEvent
        public void onClientRegistries(final FMLClientSetupEvent event)
        {
            CustomItemDecorator decorator = new CustomItemDecorator(new ResourceLocation(MODID, "test")) {
                @Override
                public void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String stackSizeLabel) {
                    RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
                    Tesselator tessellator = Tesselator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuilder();
                    int x = xOffset;
                    int y = yOffset;
                    RenderSystem.setShaderTexture(0, smiley);
                    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
                    bufferbuilder.vertex((double) (x + 0), (double) (y + 0), 0.0D).color(1.0f, 1.0f, 1.0f, 1.0f).uv(0, 0).endVertex();
                    bufferbuilder.vertex((double) (x + 0), (double) (y + 5), 0.0D).color(1.0f, 1.0f, 1.0f, 1.0f).uv(0, 1).endVertex();
                    bufferbuilder.vertex((double) (x + 5), (double) (y + 5), 0.0D).color(1.0f, 1.0f, 1.0f, 1.0f).uv(1, 1).endVertex();
                    bufferbuilder.vertex((double) (x + 5), (double) (y + 0), 0.0D).color(1.0f, 1.0f, 1.0f, 1.0f).uv(1, 0).endVertex();
                    bufferbuilder.end();
                    BufferUploader.end(bufferbuilder);
                }
            };
        }
    }
}
