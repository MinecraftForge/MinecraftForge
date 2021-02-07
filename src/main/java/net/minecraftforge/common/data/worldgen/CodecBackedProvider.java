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

package net.minecraftforge.common.data.worldgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A Data Provider that generates the files based on a Codec.
 */
public abstract class CodecBackedProvider<P> implements IDataProvider
{
    protected final static Logger LOGGER = LogManager.getLogger();
    protected final RegistryOpsHelper regOps;
    protected final Codec<P> codec;
    protected final Gson gson;

    protected CodecBackedProvider(Codec<P> codec, RegistryOpsHelper regOps)
    {
        this(codec, regOps, new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create());
    }

    protected CodecBackedProvider(Codec<P> codec, RegistryOpsHelper regOps, Gson gson)
    {
        this.codec = codec;
        this.regOps = regOps;
        this.gson = gson;
    }

    protected void save(P instance, DirectoryCache cache, Path path) throws IOException
    {
        IDataProvider.save(
                gson,
                cache,
                codec.encodeStart(regOps.getSerializer(), instance)
                        .getOrThrow(false, s -> LOGGER.error("Could not encode object for " + this.getName() + " because : " + s)),
                path
        );
    }
}
