package net.minecraftforge.common.loot;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;

/**
 * Implementation that defines what a global loot modifier must implement in order to be functional.
 * {@link LootModifier} Supplies base functionality; most modders should only need to extend it.<br/>
 * Requires an {@link IGlobalLootModifierSerializer} to be registered.
 */
public interface IGlobalLootModifier {
	/**
     * Applies the modifier to the generated loot
     * @param generatedLoot the list of ItemStacks that will be dropped, generated by loot tables
     * @param context the LootContext, identical to what is passed to loot tables
     * @return modified loot drops
     */
    List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context);
}
