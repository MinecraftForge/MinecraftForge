/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import java.io.IOException;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.event.RegisterChunkBufferLayersEvent;
import net.minecraftforge.client.event.RegisterNamedRenderTypesEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CustomChunkLayerTest.MODID)
public class CustomChunkLayerTest
{
    public static final String MODID = "custom_chunk_layer_test";
    private static final boolean ENABLE = false;

    private static RenderType TestRenderType = RenderType.create(
            "custom_chunk_layer_test:test",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            1 << 8,
            false, /* affectsCrumbling */
            false, /* sortOnUpload */
            CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(ClientHandler::getShader))
                .createCompositeState(true));

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("test", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));

    public CustomChunkLayerTest()
    {
        if (ENABLE)
        {
            BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
            ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ClientHandler
    {
        private static ShaderInstance SHADER;

        public static ShaderInstance getShader()
        {
            return SHADER;
        }

        @SubscribeEvent
        public static void onRegisterShaders(RegisterShadersEvent event) throws IOException
        {
            if (!ENABLE) return;

            event.registerShader(new ShaderInstance(
                    event.getResourceProvider(),
                    new ResourceLocation(MODID, "rendertype_test"),
                    DefaultVertexFormat.BLOCK),
                    shader -> SHADER = shader);
        }

        @SubscribeEvent
        public static void onRegisterChunkBufferLayers(RegisterChunkBufferLayersEvent event)
        {
            if (!ENABLE) return;

            event.registerSolid(RenderType.solid(), TestRenderType);
        }

        @SubscribeEvent
        public static void onRegisterNamedRenderTypes(RegisterNamedRenderTypesEvent event)
        {
            if (!ENABLE) return;

            event.register("test", TestRenderType, ForgeRenderTypes.ITEM_LAYERED_CUTOUT.get());
        }
    }
}
