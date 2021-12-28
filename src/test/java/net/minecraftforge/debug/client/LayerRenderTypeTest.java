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

package net.minecraftforge.debug.client;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.Util;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.LayerRenderTypeRegisterEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderTypeRegisterEvent;
import net.minecraftforge.client.renderer.ComplexRenderType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Mod(LayerRenderTypeTest.MOD_ID)
public class LayerRenderTypeTest
{
    private static final boolean ENABLED = true;
    static final String MOD_ID = "layer_render_type_test";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    private static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("test", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    private static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties()));

    public LayerRenderTypeTest()
    {
        if (ENABLED)
        {
            BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
            ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ClientHandler
    {
        private static ShaderInstance CUSTOM_SHADER;

        private static final ResourceLocation CUSTOM_RENDER_TYPE_NAME = new ResourceLocation(MOD_ID, "custom");
        private static RenderType CUSTOM_RENDER_TYPE;

        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event)
        {
            if (!ENABLED) return;

            ItemBlockRenderTypes.setRenderLayer(TEST_BLOCK.get(), CUSTOM_RENDER_TYPE);
        }

        @SubscribeEvent
        public static void onRegisterRenderTypes(RenderTypeRegisterEvent event)
        {
            if (!ENABLED) return;

            CUSTOM_RENDER_TYPE = ComplexRenderType.builder()
                    .beforeChunkRenderCallback(ClientHandler::beforeChunkRender)
                    .build(
                            CUSTOM_RENDER_TYPE_NAME.toString(),
                            DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS,
                            RenderType.SMALL_BUFFER_SIZE, true, false,
                            RenderType.CompositeState.builder()
                                    .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> CUSTOM_SHADER))
                                    .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
                                    .createCompositeState(true)
                    );
        }

        @SubscribeEvent
        public static void onRegisterSolidStaticRenderTypes(LayerRenderTypeRegisterEvent.Solid event)
        {
            if (!ENABLED) return;

            event.getBuilder().add(
                    CUSTOM_RENDER_TYPE, CUSTOM_RENDER_TYPE_NAME,
                    List.of(RenderType.cutout()), // After cutout
                    List.of()
            );
        }

        @SubscribeEvent
        public static void onRegisterShaders(RegisterShadersEvent event) throws IOException
        {
            if (!ENABLED) return;

            event.registerShader(
                    new ShaderInstance(event.getResourceManager(), new ResourceLocation(MOD_ID, "rendertype_custom"), DefaultVertexFormat.BLOCK),
                    shader -> CUSTOM_SHADER = shader
            );
        }

        private static void beforeChunkRender(
            LevelRenderer levelRenderer,
            ChunkRenderDispatcher.RenderChunk renderChunk,
            PoseStack poseStack,
            double cameraX,
            double cameraY,
            double cameraZ,
            Matrix4f projectionMatrix
        ) {
            final Uniform TIME = CUSTOM_SHADER.getUniform("SysTime");
            Objects.requireNonNull(TIME).set((Util.getMillis() % 1000) / 1000f);
        }
    }

}
