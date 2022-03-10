/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.resource.PathResourcePack;

import java.io.IOException;

@Mod(AddPackFinderEventTest.MODID)
@Mod.EventBusSubscriber(modid=AddPackFinderEventTest.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class AddPackFinderEventTest
{
    public static final String MODID = "add_pack_finders_test";

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event)
    {
        try
        {
            if (event.getPackType() == PackType.CLIENT_RESOURCES)
            {
                var resourcePath = ModList.get().getModFileById(MODID).getFile().findResource("test_nested_resource_pack");
                var pack = new PathResourcePack(ModList.get().getModFileById(MODID).getFile().getFileName() + ":" + resourcePath, resourcePath);
                var metadataSection = pack.getMetadataSection(PackMetadataSection.SERIALIZER);
                if (metadataSection != null)
                {
                    event.addRepositorySource((packConsumer, packConstructor) ->
                            packConsumer.accept(packConstructor.create(
                                    "builtin/add_pack_finders_test", new TextComponent("display name"), false,
                                    () -> pack, metadataSection, Pack.Position.BOTTOM, PackSource.BUILT_IN, false)));
                }
            }
        }
        catch(IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
