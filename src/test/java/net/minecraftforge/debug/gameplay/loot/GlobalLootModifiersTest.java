package net.minecraftforge.debug.gameplay.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
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
			LootModifierManager.registerFunction(new GlassTouchModifier.Serializer());
			LootModifierManager.registerFunction(new WheatSeedsModifier.Serializer());
		}
	}

	private static class SmeltingModifier extends LootModifier {
		public SmeltingModifier(ResourceLocation name, ILootCondition[] conditionsIn) {
			super(name, conditionsIn);

		}

		@Override
		public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
			ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
			generatedLoot.forEach((stack) -> ret.add(smelt(stack, context)));
			return ret;
		}

		private static ItemStack smelt(ItemStack stack, LootContext context) {
			Optional<FurnaceRecipe> optional = context.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), context.getWorld());
			if (optional.isPresent()) {
				ItemStack itemstack = optional.get().getRecipeOutput();
				if (!itemstack.isEmpty()) {
					ItemStack itemstack1 = itemstack.copy();
					itemstack1.setCount(stack.getCount() * itemstack.getCount());
					return itemstack1;
				}
			}
			return stack;
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

	private static class GlassTouchModifier extends LootModifier {
		public GlassTouchModifier(ResourceLocation name, ILootCondition[] conditionsIn) {
			super(name, conditionsIn);

		}

		@Override
		public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
			ItemStack ctxTool = context.get(LootParameters.TOOL);
			ItemStack fakeTool = ctxTool.copy();
			fakeTool.addEnchantment(Enchantments.SILK_TOUCH, 1);
			if(ItemStack.areItemsEqual(ctxTool, fakeTool) && EnchantmentHelper.getEnchantments(ctxTool).containsKey(Enchantments.SILK_TOUCH)) return generatedLoot;
			LootContext.Builder builder = new LootContext.Builder(context.getWorld())
					.withParameter(LootParameters.BLOCK_STATE, context.get(LootParameters.BLOCK_STATE))
					.withRandom(context.getWorld().rand)
					.withParameter(LootParameters.POSITION, context.get(LootParameters.POSITION))
					.withParameter(LootParameters.TOOL, fakeTool)
					.withNullableParameter(LootParameters.BLOCK_ENTITY, context.get(LootParameters.BLOCK_ENTITY));
			if(context.has(LootParameters.THIS_ENTITY)) {
				builder.withParameter(LootParameters.THIS_ENTITY, context.get(LootParameters.THIS_ENTITY));
			}
			LootContext ctx = builder.build(LootParameterSets.BLOCK);
			LootTable loottable = context.getWorld().getServer().getLootTableManager().getLootTableFromLocation(context.get(LootParameters.BLOCK_STATE).getBlock().getLootTable());
			return loottable.generate(ctx);
		}

		private static class Serializer extends LootModifier.Serializer<GlassTouchModifier> {

			public Serializer() {
				super(new ResourceLocation("global_loot_test:glass_silk_test"), GlassTouchModifier.class);
			}

			@Override
			public GlassTouchModifier deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ResourceLocation name, ILootCondition[] conditionsIn) {
				return new GlassTouchModifier(name, conditionsIn);
			}
		}
	}
	
	private static class WheatSeedsModifier extends LootModifier {
		public WheatSeedsModifier(ResourceLocation name, ILootCondition[] conditionsIn) {
			super(name, conditionsIn);

		}

		@Override
		public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
			if(context.get(LootParameters.BLOCK_STATE).getBlock() != Blocks.WHEAT) return generatedLoot;
			int numSeeds = 0;
			for(ItemStack stack : generatedLoot) {
				if(stack.getItem() == Items.WHEAT_SEEDS)
					numSeeds+=stack.getCount();
			}
			if(numSeeds >= 3) {
				generatedLoot.removeIf(x -> x.getItem() == Items.WHEAT_SEEDS);
				generatedLoot.add(new ItemStack(Items.WHEAT));
			}
			return generatedLoot;
		}
		
		private static class Serializer extends LootModifier.Serializer<WheatSeedsModifier> {

			public Serializer() {
				super(new ResourceLocation("global_loot_test:wheat_seeds_test"), WheatSeedsModifier.class);
			}

			@Override
			public WheatSeedsModifier deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ResourceLocation name, ILootCondition[] conditionsIn) {
				return new WheatSeedsModifier(name, conditionsIn);
			}
		}
	}
}
