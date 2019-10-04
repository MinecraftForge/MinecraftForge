package net.minecraftforge.common.loot;

import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.IParameterized;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public interface IGlobalLootModification {
	List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context);

	public abstract static class Serializer<T extends IGlobalLootModification> {
		private final ResourceLocation lootModifierLocation;
		private final Class<T> functionClass;

		protected Serializer(ResourceLocation location, Class<T> clazz) {
			this.lootModifierLocation = location;
			this.functionClass = clazz;
		}

		public ResourceLocation getFunctionName() {
			return this.lootModifierLocation;
		}

		public Class<T> getFunctionClass() {
			return this.functionClass;
		}

		public abstract void serialize(JsonObject object, T functionClazz, JsonSerializationContext serializationContext);

		public abstract T deserialize(JsonObject object, JsonDeserializationContext serializationContext);
	}
}
