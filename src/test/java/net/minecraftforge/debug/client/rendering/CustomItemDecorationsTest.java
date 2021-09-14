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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ItemDecorator;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.items.CapabilityItemDecoratorHandler;
import net.minecraftforge.items.ItemDecoratorHandler;
import net.minecraftforge.items.IItemDecoratorHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(CustomItemDecorationsTest.MODID)
public class CustomItemDecorationsTest {
    public static final String MODID = "render_item_decorations_test";
    private static final boolean IS_ENABLED = true;
    private static final ResourceLocation smiley = new ResourceLocation(MODID + ":textures/smiley.png");
    public static final DeferredRegister<ItemDecorator> ITEM_DECORATORS = DeferredRegister.create(ForgeRegistries.ITEM_DECORATORS, MODID);
    
    public static RegistryObject<ItemDecorator> test_decorator = ITEM_DECORATORS.register("test_decorator", () ->
            new ItemDecorator()
            {
                @Override
                @OnlyIn(Dist.CLIENT)
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
            }
    );
    
    public CustomItemDecorationsTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEM_DECORATORS.register(modEventBus);
    }
    
    @Mod.EventBusSubscriber(modid = MODID)
    public static class Capabilities
    {
        @SubscribeEvent
        public static void onAttachCapabilities(final AttachCapabilitiesEvent<ItemStack> event) {
            if (IS_ENABLED) {
                if (!(event.getObject().getItem() instanceof DiggerItem)) return;
                ItemDecoratorHandler handler = new ItemDecoratorHandler();
                handler.addDecoration(test_decorator.get(), event.getObject());
                LazyOptional<IItemDecoratorHandler> optional = LazyOptional.of(() -> handler);
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
        
        @SubscribeEvent
        public static void onEvent(final ItemTossEvent event)
        {
            if(IS_ENABLED) {
                ItemStack stack = event.getEntityItem().getItem();
                LazyOptional<IItemDecoratorHandler> lazyOptional = stack.getCapability(CapabilityItemDecoratorHandler.ITEM_DECORATOR_HANDLER_CAPABILITY);
                lazyOptional.ifPresent(decoration -> decoration.removeDecoration(test_decorator.get(), stack));
            }
        }
    }
}
