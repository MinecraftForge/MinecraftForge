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

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * This LootItemCondition "forge:can_tool_perform_action" can be used to check if a tool can perform a given ToolAction.
 */
public class CanToolPerformAction implements LootItemCondition {

    public static final LootItemConditionType LOOT_CONDITION_TYPE = new LootItemConditionType(new CanToolPerformAction.Serializer());

    final ToolAction action;

    public CanToolPerformAction(ToolAction action) {
        this.action = action;
    }

    @Nonnull
    public LootItemConditionType getType() {
        return LOOT_CONDITION_TYPE;
    }

    @Nonnull
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    public boolean test(LootContext lootContext) {
        ItemStack itemstack = lootContext.getParamOrNull(LootContextParams.TOOL);
        return itemstack != null && itemstack.canPerformAction(this.action);
    }

    public static LootItemCondition.Builder canToolPerformAction(ToolAction action) {
        return () -> new CanToolPerformAction(action);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<CanToolPerformAction> {
        public void serialize(JsonObject json, CanToolPerformAction itemCondition, @Nonnull JsonSerializationContext context) {
            json.addProperty("action", itemCondition.action.name());
        }

        @Nonnull
        public CanToolPerformAction deserialize(JsonObject json, @Nonnull JsonDeserializationContext context) {
            return new CanToolPerformAction(ToolAction.get(json.get("action").getAsString()));
        }
    }

}
