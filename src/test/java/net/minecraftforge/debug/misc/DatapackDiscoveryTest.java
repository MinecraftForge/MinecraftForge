/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.debug.misc;

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerDatapackDiscoveryEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mod("datapack_discovery_test")
@Mod.EventBusSubscriber
public class DatapackDiscoveryTest
{

    private static final Logger LOGGER = LogManager.getLogger(DatapackDiscoveryTest.class);
    private static final String NAME = "Datapack discovery test";

    @SubscribeEvent
    public static void onDatapackDiscovery(FMLServerDatapackDiscoveryEvent event)
    {
        LOGGER.debug("FMLServerDatapackDiscoveryEvent received");
        event.addPackFinder(new IPackFinder()
        {
            @Override
            public <T extends ResourcePackInfo> void func_230230_a_(Consumer<T> p_230230_1_, ResourcePackInfo.IFactory<T> p_230230_2_)
            {
                LOGGER.debug("PackFinder enumerating packs");
                p_230230_1_.accept(ResourcePackInfo.createResourcePack(NAME, true, TestResourcePack::new,
                        p_230230_2_, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.field_232625_a_));
            }
        });
    }

    public static class TestResourcePack implements IResourcePack
    {

        @Override
        public InputStream getRootResourceStream(String fileName) throws IOException
        {
            if(fileName.equals("pack.mcmeta"))
            {
                return new ByteArrayInputStream(("{\"pack\":{\"pack_format\":5,\"description\":\"" + NAME + "\"}}").getBytes(StandardCharsets.UTF_8));
            }
            throw new FileNotFoundException(fileName);
        }

        @Override
        public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException
        {
            throw new FileNotFoundException(type.getDirectoryName() + "/" + location.getNamespace() + "/" + location.getPath());
        }

        @Override
        public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn)
        {
            return Collections.emptyList();
        }

        @Override
        public boolean resourceExists(ResourcePackType type, ResourceLocation location)
        {
            return false;
        }

        @Override
        public Set<String> getResourceNamespaces(ResourcePackType type)
        {
            return Collections.emptySet();
        }

        @Nullable
        @Override
        public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException
        {
            return ResourcePack.getResourceMetadata(deserializer, getRootResourceStream("pack.mcmeta"));
        }

        @Override
        public String getName()
        {
            return NAME;
        }

        @Override
        public void close()
        {}

    }

}
