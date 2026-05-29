/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources.PathResourcesSupplier;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftsingularity.event.AddPackFindersEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.ModList;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AddPackFinderEventTest.MODID)
public class AddPackFinderEventTest {
    private static final boolean ENABLE = false;
    public static final String MODID = "add_pack_finders_test";

    public AddPackFinderEventTest(FMLJavaModLoadingContext context) {
        if (!ENABLE)
            return;

        context.getModBusGroup().register(MethodHandles.lookup(), this);
    }

    @SubscribeEvent
    public void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            var resourcePath = ModList.getModFileById(MODID).getFile().findResource("test_nested_resource_pack");
            var supplier = new PathResourcesSupplier(resourcePath, false);
            var pack = Pack.readMetaAndCreate("builtin/add_pack_finders_test", Component.literal("display name"), false, supplier, PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, PackSource.BUILT_IN);
            event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
        }
    }
}
