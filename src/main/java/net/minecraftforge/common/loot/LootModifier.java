package net.minecraftforge.common.loot;

import java.util.List;
import java.util.function.Predicate;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;

public abstract class LootModifier implements IGlobalLootModifier {
    protected final ILootCondition[] conditions;
    private final Predicate<LootContext> combinedConditions;
    
    protected LootModifier(ILootCondition[] conditionsIn) {
        //registryName = name;
        this.conditions = conditionsIn;
        this.combinedConditions = LootConditionManager.and(conditionsIn);
    }
    
    @Override
    public final List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context) {
        return this.combinedConditions.test(context) ? this.doApply(generatedLoot, context) : generatedLoot;
    }

    protected abstract List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context);
    
    public abstract static class Serializer<T extends LootModifier> implements IGlobalLootModifierSerializer<T> {
        private ResourceLocation registryName = null;
        
        @Override
        public IGlobalLootModifierSerializer<?> setRegistryName(ResourceLocation name) {
            registryName = name;
            return this;
        }

        @Override
        public ResourceLocation getRegistryName() {
            return registryName;
        }

        @Override
        public abstract T read(ResourceLocation name, JsonObject json, ILootCondition[] conditionsIn);
    }
    
}
