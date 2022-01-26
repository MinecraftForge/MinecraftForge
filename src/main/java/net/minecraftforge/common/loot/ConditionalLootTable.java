package net.minecraftforge.common.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConditionalLootTable extends LootTable
{
    private final LootTable lootTable;
    private final List<ICondition> conditions;

    private ConditionalLootTable(final LootTable lootTable, final List<ICondition> conditions)
    {
        super(lootTable.getParamSet(), new LootPool[]{}, new LootItemFunction[]{});
        this.lootTable = lootTable;
        this.conditions = conditions;
    }

    public static void serialize(final ConditionalLootTable conditionalLootTable, final JsonObject jsonObject)
    {
        final JsonArray conditions = new JsonArray();

        for (final ICondition condition : conditionalLootTable.getConditions())
        {
            conditions.add(CraftingHelper.serialize(condition));
        }

        jsonObject.add("forge:conditions", conditions);
    }

    public List<ICondition> getConditions()
    {
        return this.conditions;
    }

    public LootTable getLootTable()
    {
        return this.lootTable;
    }

    @Override
    public void getRandomItemsRaw(@Nonnull final LootContext context, @Nonnull final Consumer<ItemStack> consumer)
    {
        this.lootTable.getRandomItemsRaw(context, consumer);
    }

    @Override
    public void getRandomItems(@Nonnull final LootContext context, @Nonnull final Consumer<ItemStack> consumer)
    {
        this.lootTable.getRandomItems(context, consumer);
    }

    @Nonnull
    @Override
    public List<ItemStack> getRandomItems(@Nonnull final LootContext lootContext)
    {
        return this.lootTable.getRandomItems(lootContext);
    }

    @Override
    public void validate(@Nonnull final ValidationContext context)
    {
        this.lootTable.validate(context);
    }

    @Override
    public void fill(@Nonnull final Container container, @Nonnull final LootContext context)
    {
        this.lootTable.fill(container, context);
    }

    @Override
    public void freeze()
    {
        this.lootTable.freeze();
    }

    @Override
    public boolean isFrozen()
    {
        return this.lootTable.isFrozen();
    }

    @Override
    @Nonnull
    public ResourceLocation getLootTableId()
    {
        return this.lootTable.getLootTableId();
    }

    @Override
    public void setLootTableId(@Nonnull final ResourceLocation id)
    {
        this.lootTable.setLootTableId(id);
    }

    @Nonnull
    @Override
    public LootPool getPool(@Nonnull final String name)
    {
        return this.lootTable.getPool(name);
    }

    @Nonnull
    @Override
    public LootPool removePool(@Nonnull final String name)
    {
        return this.lootTable.removePool(name);
    }

    @Override
    public void addPool(@Nonnull final LootPool pool)
    {
        this.lootTable.addPool(pool);
    }

    public static class Builder extends LootTable.Builder
    {
        private final LootTable lootTable;
        private final List<ICondition> conditions = new ArrayList<>();

        public Builder(final LootTable lootTable)
        {
            this.lootTable = lootTable;
        }

        public ConditionalLootTable.Builder addCondition(final ICondition condition)
        {
            this.conditions.add(condition);
            return this;
        }

        @Nonnull
        @Override
        public LootTable build()
        {
            return new ConditionalLootTable(this.lootTable, this.conditions);
        }
    }
}
