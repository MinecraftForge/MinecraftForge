package net.minecraftforge.common.loot;

import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Implementation that defines the deserialization process for global loot modifier data files.
 * {@link LootModifier.Serializer} Supplies base functionality; most modders should only need to extend it.
 * @param <T> the Type to deserialize
 */
public interface IGlobalLootModifierSerializer<T extends IGlobalLootModifier> extends IForgeRegistryEntry<IGlobalLootModifierSerializer<?>> {
    /**
     * Most mods will likely not need more than<br/>
     * <code>return new MyModifier(name, conditionsIn)</code><br/>
     * but deserializing any additional properties that are needed is done here.
     * @param name The resource location
     * @param json The full json object
     * @param conditionsIn An already deserialized list of ILootConditions.
     */
    public abstract T read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition);

    /**
     * Used by Forge's registry system.
     */
    @Override
    public Class<IGlobalLootModifierSerializer<?>> getRegistryType();
    
}
