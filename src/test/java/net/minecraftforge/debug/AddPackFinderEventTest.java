/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;

@Mod(AddPackFinderEventTest.MODID)
public class AddPackFinderEventTest
{
    private static final boolean ENABLE = false;
    public static final String MODID = "add_pack_finders_test";

    public AddPackFinderEventTest() {
        if (!ENABLE)
            return;

        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void addPackFinders(AddPackFindersEvent event)
    {
        if (event.getPackType() == PackType.CLIENT_RESOURCES)
        {
            var resourcePath = ModList.get().getModFileById(MODID).getFile().findResource("test_nested_resource_pack");
            var pack = Pack.readMetaAndCreate("builtin/add_pack_finders_test", Component.literal("display name"), false,
                    (path) -> new PathPackResources(path, resourcePath, false), PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, PackSource.BUILT_IN);
            event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
        }
    }
}
