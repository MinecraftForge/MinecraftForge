package net.minecraftforge.common.loot;

import java.util.List;
import java.util.function.Predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;

public abstract class LootModifier implements IGlobalLootModification {
	protected final ILootCondition[] conditions;
	private final Predicate<LootContext> combinedConditions;
	public static final ResourceLocation EMPTY = new ResourceLocation("forge","empty");
	public static final LootModifier EMPTY_LOOT_MOD = new EmptyModifier();
	public final ResourceLocation registryName;

	protected LootModifier(ResourceLocation name, ILootCondition[] conditionsIn) {
		registryName = name;
		this.conditions = conditionsIn;
		this.combinedConditions = LootConditionManager.and(conditionsIn);
	}

	public final List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context) {
		return this.combinedConditions.test(context) ? this.doApply(generatedLoot, context) : generatedLoot;
	}

	protected abstract List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context);

	public abstract static class Serializer<T extends LootModifier> extends IGlobalLootModification.Serializer<T> {
		public Serializer(ResourceLocation location, Class<T> clazz) {
			super(location, clazz);
		}

		public void serialize(JsonObject object, T functionClazz, JsonSerializationContext serializationContext) {

		}

		public final T deserialize(JsonObject p_212870_1_, JsonDeserializationContext p_212870_2_) {
			ILootCondition[] ailootcondition = JSONUtils.deserializeClass(p_212870_1_, "conditions", new ILootCondition[0], p_212870_2_, ILootCondition[].class);
			ResourceLocation name = new ResourceLocation(JSONUtils.getString(p_212870_1_, "function"));
			return (T)this.deserialize(p_212870_1_, p_212870_2_, name, ailootcondition);
		}

		public abstract T deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ResourceLocation name, ILootCondition[] conditionsIn);
	}
}
