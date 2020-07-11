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

package net.minecraftforge.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.advancements.Advancement;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class ConditionalAdvancement
{
    public static Builder builder()
    {
        return new Builder();
    }

    public static boolean processConditions(JsonObject json) {
        JsonArray entries = JSONUtils.getJsonArray(json, "advancements", null);
        if (entries == null)
        {
            return CraftingHelper.processConditions(json, "conditions");
        }

        int idx = 0;
        for (JsonElement ele : entries)
        {
            if (!ele.isJsonObject())
                throw new JsonSyntaxException("Invalid advancement entry at index " + idx + " Must be JsonObject");
            if (CraftingHelper.processConditions(JSONUtils.getJsonArray(ele.getAsJsonObject(), "conditions")))
                return true;
            idx++;
        }
        return false;
    }

    public static class Builder
    {
        private List<ICondition[]> conditions = new ArrayList<>();
        private List<Advancement.Builder> advancements = new ArrayList<>();

        private List<ICondition> currentConditions = new ArrayList<>();
        private boolean locked = false;

        public Builder addCondition(ICondition condition)
        {
            if (locked)
                throw new IllegalStateException("Attempted to modify finished builder");
            currentConditions.add(condition);
            return this;
        }

        public Builder addAdvancement(Consumer<Consumer<Advancement.Builder>> callable)
        {
            if (locked)
                throw new IllegalStateException("Attempted to modify finished builder");
            callable.accept(this::addAdvancement);
            return this;
        }

        public Builder addAdvancement(Advancement.Builder recipe)
        {
            if (locked)
                throw new IllegalStateException("Attempted to modify finished builder");
            if (currentConditions.isEmpty())
                throw new IllegalStateException("Can not add a advancement with no conditions.");
            conditions.add(currentConditions.toArray(new ICondition[currentConditions.size()]));
            advancements.add(recipe);
            currentConditions.clear();
            return this;
        }

        public JsonObject write()
        {
            if (!locked)
            {
                if (!currentConditions.isEmpty())
                    throw new IllegalStateException("Invalid builder state: Orphaned conditions");
                if (advancements.isEmpty())
                    throw new IllegalStateException("Invalid builder state: No Advancements");
                locked = true;
            }
            JsonObject json = new JsonObject();
            JsonArray array = new JsonArray();
            json.add("advancements", array);
            for (int x = 0; x < conditions.size(); x++)
            {
                JsonObject holder = new JsonObject();

                JsonArray conds = new JsonArray();
                for (ICondition c : conditions.get(x))
                    conds.add(CraftingHelper.serialize(c));
                holder.add("conditions", conds);
                holder.add("advancement", advancements.get(x).serialize());

                array.add(holder);
            }
            return json;
        }
    }
}
