/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

public class LootTableIdCondition implements LootItemCondition
{
    // TODO Forge Registry at some point?
    public static final LootItemConditionType LOOT_TABLE_ID = new LootItemConditionType(new LootTableIdCondition.Serializer());
    public static final ResourceLocation UNKNOWN_LOOT_TABLE = new ResourceLocation("forge", "unknown_loot_table");

    private final ResourceLocation targetLootTableId;

    private LootTableIdCondition(final ResourceLocation targetLootTableId)
    {
        this.targetLootTableId = targetLootTableId;
    }

    @Override
    public LootItemConditionType getType()
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

    public static class Builder implements LootItemCondition.Builder
    {
        private final ResourceLocation targetLootTableId;

        public Builder(ResourceLocation targetLootTableId)
        {
            if (targetLootTableId == null) throw new IllegalArgumentException("Target loot table must not be null");
            this.targetLootTableId = targetLootTableId;
        }

        @Override
        public LootItemCondition build()
        {
            return new LootTableIdCondition(this.targetLootTableId);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootTableIdCondition>
    {
        @Override
        public void serialize(JsonObject object, LootTableIdCondition instance, JsonSerializationContext ctx)
        {
            object.addProperty("loot_table_id", instance.targetLootTableId.toString());
        }

        @Override
        public LootTableIdCondition deserialize(JsonObject object, JsonDeserializationContext ctx)
        {
            return new LootTableIdCondition(new ResourceLocation(GsonHelper.getAsString(object, "loot_table_id")));
        }
    }
}
