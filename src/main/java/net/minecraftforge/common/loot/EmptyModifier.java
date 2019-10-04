package net.minecraftforge.common.loot;

import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class EmptyModifier extends LootModifier {

	protected EmptyModifier() {
		super(LootModifier.EMPTY, new ILootCondition[] {});
	}

	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		return generatedLoot;
	}
}
