package net.minecraftforge.common.data.worldgen;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.resources.*;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenSettingsExport;
import net.minecraft.util.registry.WorldSettingsImport;
import net.minecraftforge.common.data.worldgen.RegistryBackedProvider;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Helper to deal with properly referencing objects instead of
 * inlining them everywhere.
 *
 * Creates a custom {@link DynamicRegistries.Impl} based on the vanilla objects and
 * any objects found in the paths specified by the '--input' arg.
 * It is then used to create a {@link WorldGenSettingsExport} that will be able to
 * prevent inlining objects.
 */
public class RegistryOpsHelper
{
    /**
     * The vanilla worldgen registries as a {@link DynamicRegistries}
     * plus any registry objects in the path(s) specified using '--input'
     */
    private final DynamicRegistries.Impl dynamicRegistries;
    private final WorldGenSettingsExport<JsonElement> serializer;

    /**
     * @param inputs inputs based on the arg, registry objects contained in it will be registered
     *               and can be retrieved properly.
     */
    public RegistryOpsHelper(Collection<Path> inputs)
    {
        SimpleReloadableResourceManager inputResources = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
        for (Path existing : inputs)
        {
            File file = existing.toFile();
            IResourcePack pack = file.isDirectory() ? new FolderPack(file) : new FilePack(file);
            inputResources.addResourcePack(pack);
        }
        //this dynamic registry impl contains all vanilla objects and not the recreated ones.
        dynamicRegistries = DynamicRegistries.field_243600_c;
        //This functions loads all of the registry objects present in the resourcepacks
        // and registers them into the passed in dynamic registry.
        WorldSettingsImport.func_244335_a(JsonOps.INSTANCE, inputResources, dynamicRegistries);
        serializer = WorldGenSettingsExport.func_240896_a_(JsonOps.INSTANCE, dynamicRegistries);
    }

    /**
     * The operations done are identical to {@link JsonOps#INSTANCE} except when
     * encoding using a {@link RegistryKeyCodec}.
     *
     * It will check against the registries in {@link #dynamicRegistries} for the object to encode.
     * If the object to encode is registered, it will not decode it completely but instead reference it by it's registry name.
     */
    public WorldGenSettingsExport<JsonElement> getSerializer()
    {
        return serializer;
    }

    /**
     * Register a genned object to the appropriate dynamic registry.
     * This should not be used by modders, it is already accounted for by {@link RegistryBackedProvider#saveAndRegister}
     */
    public <T> void registerObject(RegistryKey<? extends Registry<T>> regKey, ResourceLocation name, T obj)
    {
        dynamicRegistries.func_243612_b(regKey).register(RegistryKey.func_240903_a_(regKey, name), obj, Lifecycle.experimental());
    }

    /**
     * Get an object based on a registry's key and an resource location.
     * This object must have been registered before hand, so either:
     * - It is a vanilla reference (although that is useless, referencing the object directly works)
     * - It is a previously data genned object.
     * - It is a data object retrieved through the '--input' arg.
     *
     * @param regKey    registry key of the registry
     * @param objName   name of the object
     * @param <T>       Type of the object
     * @return          The object.
     */
    public <T> T getObject(RegistryKey<? extends Registry<T>> regKey, ResourceLocation objName)
    {
        return dynamicRegistries.func_243612_b(regKey).func_241873_b(objName)
                .orElseThrow(() -> new RuntimeException("Could not retrieve " + objName + " in reg " + regKey));
    }

    public <T> T getObject(RegistryKey<? extends Registry<T>> regKey, RegistryKey<T> objKey)
    {
        return getObject(regKey, objKey.func_240901_a_());
    }
}
