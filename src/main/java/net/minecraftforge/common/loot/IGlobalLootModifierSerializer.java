package net.minecraftforge.common.loot;

import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IGlobalLootModifierSerializer<T extends IGlobalLootModifier> extends IForgeRegistryEntry<IGlobalLootModifierSerializer<?>> {
    public abstract T read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition);

    @Override
    public default Class<IGlobalLootModifierSerializer<?>> getRegistryType() {
        return null;
    }
    
}
