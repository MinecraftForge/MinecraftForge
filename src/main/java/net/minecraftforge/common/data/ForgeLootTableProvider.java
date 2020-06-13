package net.minecraftforge.common.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.Alternative;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.Inverted;
import net.minecraft.world.storage.loot.conditions.MatchTool;
import net.minecraftforge.common.Tags;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ForgeLootTableProvider extends LootTableProvider {

    public ForgeLootTableProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        // do not validate against all registered loot tables
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return super.getTables().stream().map(pair -> {
            // provides new consumer with filtering only changed loot tables and replacing condition item to condition tag
            return new Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>(() -> replaceAndFilterChangesOnly(pair.getFirst().get()), pair.getSecond());
        }).collect(Collectors.toList());
    }

    private Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> replaceAndFilterChangesOnly(Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> consumer) {
        return (newConsumer) -> consumer.accept((resourceLocation, builder) -> {
            if (findAndReplaceInLootTableBuilder(builder, Items.SHEARS, Tags.Items.SHEARS)) {
                newConsumer.accept(resourceLocation, builder);
            }
        });
    }

    private boolean findAndReplaceInLootTableBuilder(LootTable.Builder builder, Item from, Tag<Item> to) {
        List<LootPool> lootPools = getField(LootTable.Builder.class, builder, 0);
        boolean found = false;

        for (LootPool lootPool : lootPools) {
            if (findAndReplaceInLootPool(lootPool, from, to)) {
                found = true;
            }
        }

        return found;
    }

    private boolean findAndReplaceInLootPool(LootPool lootPool, Item from, Tag<Item> to) {
        List<LootEntry> lootEntries = getField(LootPool.class, lootPool, 1);
        List<ILootCondition> lootConditions = getField(LootPool.class, lootPool, 2);
        boolean found = false;

        for (LootEntry lootEntry : lootEntries) {
            if (lootEntry instanceof ParentedLootEntry) {
                if (findAndReplaceInParentedLootEntry((ParentedLootEntry) lootEntry, from, to)) {
                    found = true;
                }
            }
        }

        for (int i = 0; i < lootConditions.size(); i++) {
            ILootCondition lootCondition = lootConditions.get(i);
            if (lootCondition instanceof MatchTool && checkMatchTool((MatchTool) lootCondition, from)) {
                lootConditions.set(i, MatchTool.builder(ItemPredicate.Builder.create().tag(to)).build());
                found = true;
            } else if (lootCondition instanceof Inverted) {
                ILootCondition invLootCondition = getField(Inverted.class, (Inverted) lootCondition, 0);

                if (invLootCondition instanceof MatchTool && checkMatchTool((MatchTool) invLootCondition, from)) {
                    lootConditions.set(i, Inverted.builder(MatchTool.builder(ItemPredicate.Builder.create().tag(to))).build());
                    found = true;
                } else if (invLootCondition instanceof Alternative && findAndReplaceInAlternative((Alternative) invLootCondition, from, to)) {
                    found = true;
                }
            }
        }

        return found;
    }

    private boolean findAndReplaceInParentedLootEntry(ParentedLootEntry entry, Item from, Tag<Item> to) {
        LootEntry[] lootEntries = getField(ParentedLootEntry.class, entry, 0);
        boolean found = false;

        for (LootEntry lootEntry : lootEntries) {
            if (findAndReplaceInLootEntry(lootEntry, from, to)) {
                found = true;
            }
        }

        return found;
    }

    private boolean findAndReplaceInLootEntry(LootEntry entry, Item from, Tag<Item> to) {
        ILootCondition[] lootConditions = getField(LootEntry.class, entry, 0);
        boolean found = false;

        for (int i = 0; i < lootConditions.length; i++) {
            if (lootConditions[i] instanceof Alternative && findAndReplaceInAlternative((Alternative) lootConditions[i], from, to)) {
                found = true;
            } else if (lootConditions[i] instanceof MatchTool && checkMatchTool((MatchTool) lootConditions[i], from)) {
                lootConditions[i] = MatchTool.builder(ItemPredicate.Builder.create().tag(to)).build();
                found = true;
            }
        }

        return found;
    }

    private boolean findAndReplaceInAlternative(Alternative alternative, Item from, Tag<Item> to) {
        ILootCondition[] lootConditions = getField(Alternative.class, alternative, 0);
        boolean found = false;

        for (int i = 0; i < lootConditions.length; i++) {
            if (lootConditions[i] instanceof MatchTool && checkMatchTool((MatchTool) lootConditions[i], from)) {
                lootConditions[i] = MatchTool.builder(ItemPredicate.Builder.create().tag(to)).build();
                found = true;
            }
        }

        return found;
    }

    private boolean checkMatchTool(MatchTool lootCondition, Item expected) {
        ItemPredicate predicate = getField(MatchTool.class, lootCondition, 0);
        Item item = getField(ItemPredicate.class, predicate, 4);
        return item != null && item.equals(expected);
    }

    @SuppressWarnings("unchecked")
    private <T, R> R getField(Class<T> clz, T inst, int index)
    {
        Field fld = clz.getDeclaredFields()[index];
        fld.setAccessible(true);
        try
        {
            return (R)fld.get(inst);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
