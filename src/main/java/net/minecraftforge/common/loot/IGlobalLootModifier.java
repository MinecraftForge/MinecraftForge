package net.minecraftforge.common.loot;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;

public interface IGlobalLootModifier {
    List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context);
}
