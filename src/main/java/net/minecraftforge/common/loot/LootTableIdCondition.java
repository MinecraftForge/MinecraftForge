/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class LootTableIdCondition implements ILootCondition
{
    // TODO Forge Registry at some point?
    public static final LootConditionType LOOT_TABLE_ID = new LootConditionType(new LootTableIdCondition.Serializer());
    public static final ResourceLocation UNKNOWN_LOOT_TABLE = new ResourceLocation("forge", "unknown_loot_table");

    private final ResourceLocation targetLootTableId;

    private LootTableIdCondition(final ResourceLocation targetLootTableId)
    {
        this.targetLootTableId = targetLootTableId;
    }

    @Override
    public LootConditionType getType()
    {
        return LOOT_TABLE_ID;
    }

    @Override
    public boolean test(LootContext lootContext)
    {
        return lootContext.getQueriedLootTableId().equals(this.targetLootTableId);
    }

    public static Builder builder(final ResourceLocation targetLootTableId)
    {
        return new Builder(targetLootTableId);
    }

    public static class Builder implements ILootCondition.IBuilder
    {
        private final ResourceLocation targetLootTableId;

        public Builder(ResourceLocation targetLootTableId)
        {
            if (targetLootTableId == null) throw new IllegalArgumentException("Target loot table must not be null");
            this.targetLootTableId = targetLootTableId;
        }

        @Override
        public ILootCondition build()
        {
            return new LootTableIdCondition(this.targetLootTableId);
        }
    }

    public static class Serializer implements ILootSerializer<LootTableIdCondition>
    {
        @Override
        public void serialize(JsonObject object, LootTableIdCondition instance, JsonSerializationContext ctx)
        {
            object.addProperty("loot_table_id", instance.targetLootTableId.toString());
        }

        @Override
        public LootTableIdCondition deserialize(JsonObject object, JsonDeserializationContext ctx)
        {
            return new LootTableIdCondition(new ResourceLocation(JSONUtils.getAsString(object, "loot_table_id")));
        }
    }
}
