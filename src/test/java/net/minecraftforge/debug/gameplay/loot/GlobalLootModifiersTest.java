package net.minecraftforge.debug.gameplay.loot;

import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootModifierManager;
import net.minecraftforge.fml.common.Mod;

@Mod("global_loot_test")
public class GlobalLootModifiersTest {
	public static final boolean ENABLE = true;
	public GlobalLootModifiersTest() {
		if (ENABLE)
		{
			LootModifierManager.registerFunction(new SmeltingModifier.Serializer());
		}
	}

	private static class SmeltingModifier extends LootModifier {

		public SmeltingModifier(ResourceLocation name, ILootCondition[] conditionsIn) {
			super(name, conditionsIn);
		}

		@Override
		public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
			return generatedLoot;
		}

		private static class Serializer extends LootModifier.Serializer<SmeltingModifier> {

			public Serializer() {
				super(new ResourceLocation("global_loot_test:smelting_test"), SmeltingModifier.class);
			}

			@Override
			public SmeltingModifier deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ResourceLocation name, ILootCondition[] conditionsIn) {
				return new SmeltingModifier(name, conditionsIn);
			}
		}
	}
}
