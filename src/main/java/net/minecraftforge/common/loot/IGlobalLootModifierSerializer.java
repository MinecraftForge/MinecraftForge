package net.minecraftforge.common.loot;

import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Implementation that defines the deserialization process for global loot modifier data files.
 * {@link LootModifier.Serializer} Supplies base functionality; most modders should only need to extend it.<br/>
 * Supply an override for forge:loot_modifiers/global_loot_modifiers to point towards your serialized data asset.
 * @param <T> the Type to deserialize
 */
public interface IGlobalLootModifierSerializer<T extends IGlobalLootModifier> extends IForgeRegistryEntry<IGlobalLootModifierSerializer<?>> {
    /**
     * Most mods will likely not need more than<br/>
     * <code>return new MyModifier(conditionsIn)</code><br/>
     * but deserializing any additional properties that are needed is done here.
     * @param name The resource location (if needed)
     * @param json The full json object, including ILootConditions information
     * @param conditionsIn An already deserialized list of ILootConditions
     */
    public abstract T read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition);

    /**
     * Used by Forge's registry system.
     */
    @Override
    public Class<IGlobalLootModifierSerializer<?>> getRegistryType();
    
}
