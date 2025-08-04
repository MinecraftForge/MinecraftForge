/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterNamedRenderTypesEvent;
import net.minecraftforge.client.model.DynamicFluidContainerModel;
import net.minecraftforge.client.model.ItemLayerGeometry;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = "forge")
public class ClientForgeMod {
    @SubscribeEvent
    public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("empty", (json, ctx) -> UnbakedGeometry.EMPTY);
        event.register("obj", ObjLoader.INSTANCE);
        event.register("fluid_container", DynamicFluidContainerModel.Loader.INSTANCE);
        event.register("item_layers", ItemLayerGeometry.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ObjLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterNamedRenderTypes(RegisterNamedRenderTypesEvent event) {
        event.register("item_unlit", ChunkSectionLayer.TRANSLUCENT, ForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get());
    }
}
