/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class LootTableIdCondition implements ILootCondition {
    public static final ResourceLocation UNKNOWN_LOOT_TABLE = new ResourceLocation("forge", "unknown");

    private final ResourceLocation targetLootTableId;

    private LootTableIdCondition(final ResourceLocation targetLootTableId) {
        this.targetLootTableId = targetLootTableId;
    }

    public static Builder builder(final ResourceLocation targetLootTableId) {
        return new Builder(targetLootTableId);
    }

    @Override
    public LootConditionType func_230419_b_() {
        return LootConditionManager.lootTableId;
    }

    @Override
    public boolean test(LootContext lootContext) {
        final ResourceLocation queriedLootTableId = lootContext.getQueriedLootTableId();
        return queriedLootTableId != null && queriedLootTableId.equals(this.targetLootTableId);
    }

    public static class Builder implements ILootCondition.IBuilder {
        private final ResourceLocation targetLootTableId;

        public Builder(ResourceLocation targetLootTableId) {
            if (targetLootTableId == null) throw new IllegalArgumentException("Target loot table must not be null");
            this.targetLootTableId = targetLootTableId;
        }

        @Override
        public ILootCondition build() {
            return new LootTableIdCondition(this.targetLootTableId);
        }
    }

    public static class Serializer implements ILootSerializer<LootTableIdCondition> {
        @Override
        public void func_230424_a_(JsonObject object, LootTableIdCondition instance, JsonSerializationContext serializationContext) {
            object.addProperty("loot_table_id", instance.targetLootTableId.toString());
        }

        @Override
        public LootTableIdCondition func_230423_a_(JsonObject object, JsonDeserializationContext deserializationContext) {
            return new LootTableIdCondition(new ResourceLocation(JSONUtils.getString(object, "loot_table_id")));
        }
    }
}
