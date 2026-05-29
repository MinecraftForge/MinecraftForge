/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.api.distmarker.Dist;
import net.minecraftsingularity.client.event.ModelEvent;
import net.minecraftsingularity.client.event.RegisterClientReloadListenersEvent;
import net.minecraftsingularity.client.event.RegisterNamedRenderTypesEvent;
import net.minecraftsingularity.client.model.DynamicFluidContainerModel;
import net.minecraftsingularity.client.model.obj.ObjLoader;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "singularity")
public class ClientForgeMod {
    @SubscribeEvent
    public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(singularityRL("empty"), (json, ctx) -> UnbakedGeometry.EMPTY);
        event.register(singularityRL("obj"), ObjLoader.INSTANCE);
        event.register(singularityRL("fluid_container"), DynamicFluidContainerModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ObjLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterNamedRenderTypes(RegisterNamedRenderTypesEvent event) {
        event.register(singularityRL("item_unlit"), ChunkSectionLayer.TRANSLUCENT, singularityRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get());
    }

    private static Identifier singularityRL(String path) {
        return Identifier.fromNamespaceAndPath("singularity", path);
    }
}
