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

import com.mojang.serialization.Codec;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A provider can be backed by a registry if its registry is available
 * in {@link DynamicRegistries}. For now there are only 9 from vanilla.
 */
public abstract class RegistryBackedProvider<P> extends CodecBackedProvider<P>
{
    private final RegistryKey<? extends Registry<P>> regKey;

    protected RegistryBackedProvider(Codec<P> codec, RegistryOpsHelper regOps, RegistryKey<? extends Registry<P>> regKey)
    {
        super(codec, regOps);
        this.regKey = regKey;
    }

    protected void saveAndRegister(P instance, ResourceLocation name, DirectoryCache cache, Path path) throws IOException
    {
        if(instance instanceof IForgeRegistryEntry)
            ((IForgeRegistryEntry<?>) instance).setRegistryName(name);
        this.save(instance, cache, path);
        regOps.registerObject(regKey, name, instance);
    }
}
