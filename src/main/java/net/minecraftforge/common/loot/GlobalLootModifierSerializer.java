package net.minecraftforge.common.loot;

import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Abstract base deserializer for LootModifiers. Takes care of Forge registry things.<br/>
 * Modders should extend this class to return their modifier and implement the abstract
 * <code>read</code> method to deserialize from json.
 * @param <T> the Type to deserialize
 */
public abstract class GlobalLootModifierSerializer<T extends IGlobalLootModifier> implements IForgeRegistryEntry<GlobalLootModifierSerializer<?>> {
    private ResourceLocation registryName = null;
    
    public final GlobalLootModifierSerializer<T> setRegistryName(String name) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        this.registryName = GameData.checkPrefix(name, true);
        return this;
    }
    
    //Helpers
    @Override
    public final GlobalLootModifierSerializer<T> setRegistryName(ResourceLocation name){ return setRegistryName(name.toString()); }

    public final GlobalLootModifierSerializer<T> setRegistryName(String modID, String name){ return setRegistryName(modID + ":" + name); }

    @Override
    public final ResourceLocation getRegistryName() {
        return registryName;
    }
    
    /**
     * Most mods will likely not need more than<br/>
     * <code>return new MyModifier(conditionsIn)</code><br/>
     * but any additional properties that are needed will need to be deserialized here.
     * @param name The resource location (if needed)
     * @param json The full json object (including ILootConditions)
     * @param conditionsIn An already deserialized list of ILootConditions
     */
    public abstract T read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition);

    /**
     * Used by Forge's registry system.
     */
    @Override
    public final Class<GlobalLootModifierSerializer<?>> getRegistryType() {
        return castClass(GlobalLootModifierSerializer.class);
    }
    
    @SuppressWarnings("unchecked") // Need this wrapper, because generics
    private static <G> Class<G> castClass(Class<?> cls)
    {
        return (Class<G>)cls;
    }
}
