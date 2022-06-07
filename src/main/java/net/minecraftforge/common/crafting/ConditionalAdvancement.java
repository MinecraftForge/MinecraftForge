/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class ConditionalAdvancement
{
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Processes the conditional advancement during loading.
     * @param json The incoming json from the advancement file.
     * @return The advancement that passed the conditions, or null if none did.
     */
    @Nullable
    public static JsonObject processConditional(JsonObject json, ICondition.IContext context) {
        JsonArray entries = GsonHelper.getAsJsonArray(json, "advancements", null);
        if (entries == null)
        {
            return CraftingHelper.processConditions(json, "conditions", context) ? json : null;
        }

        int idx = 0;
        for (JsonElement ele : entries)
        {
            if (!ele.isJsonObject())
                throw new JsonSyntaxException("Invalid advancement entry at index " + idx + " Must be JsonObject");
            if (CraftingHelper.processConditions(GsonHelper.getAsJsonArray(ele.getAsJsonObject(), "conditions"), context))
                return GsonHelper.getAsJsonObject(ele.getAsJsonObject(), "advancement");
            idx++;
        }
        return null;
    }

    public static class Builder
    {
        private List<ICondition[]> conditions = new ArrayList<>();
        private List<Supplier<JsonElement>> advancements = new ArrayList<>();

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

        public Builder addAdvancement(Advancement.Builder advancement)
        {
            return addAdvancement(advancement::serializeToJson);
        }

        public Builder addAdvancement(FinishedRecipe fromRecipe)
        {
            return addAdvancement(fromRecipe::serializeAdvancement);
        }

        private Builder addAdvancement(Supplier<JsonElement> jsonSupplier)
        {
            if (locked)
                throw new IllegalStateException("Attempted to modify finished builder");
            if (currentConditions.isEmpty())
                throw new IllegalStateException("Can not add a advancement with no conditions.");
            conditions.add(currentConditions.toArray(new ICondition[currentConditions.size()]));
            advancements.add(jsonSupplier);
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
                holder.add("advancement", advancements.get(x).get());

                array.add(holder);
            }
            return json;
        }
    }
}
