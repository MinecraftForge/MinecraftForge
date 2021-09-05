package net.minecraftforge.common.world;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public abstract class StructurePoolModifierSerializer<T extends IStructurePoolModifier> implements IForgeRegistryEntry<StructurePoolModifierSerializer<?>> {

    private ResourceLocation registryName = null;

    /**
     * This is where the json structure is deserialized.
     * All properties relevant to the modifier should be read from the json data here.
     * @param location The resource location (if needed)
     * @param json The full json object
     */
    public abstract T read(ResourceLocation location, JsonObject json);

    /**
     * Write the serializer to json.
     */
    public abstract JsonObject write(T instance);

    public StructurePoolModifierSerializer<T> setRegistryName(String name) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        this.registryName = GameData.checkPrefix(name, true);
        return this;
    }

    @Override
    public StructurePoolModifierSerializer<T> setRegistryName(ResourceLocation name) {
        return setRegistryName(name.toString());
    }

    public final StructurePoolModifierSerializer<T> setRegistryName(String modID, String name){ return setRegistryName(modID + ":" + name); }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    @Override
    public Class<StructurePoolModifierSerializer<?>> getRegistryType() {
        return castClass(StructurePoolModifierSerializer.class);
    }

    @SuppressWarnings("unchecked") // Need this wrapper, because generics
    private static <G> Class<G> castClass(Class<?> cls)
    {
        return (Class<G>)cls;
    }

}
