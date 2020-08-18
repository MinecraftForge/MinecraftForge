package net.minecraftforge.common.data.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;

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
        this.save(instance, cache, path);
        regOps.registerObject(regKey, name, instance);
    }
}
