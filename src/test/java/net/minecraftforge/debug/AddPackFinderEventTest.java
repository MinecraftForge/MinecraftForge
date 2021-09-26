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
import net.minecraftforge.fmllegacy.packs.PathResourcePack;

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
                var pack = new PathResourcePack(resourcePath);
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
