/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.conditions.ICondition;

/**
 * A `ConditionalAdvancement` is a single advancment file that contains multiple advancements, each having a condition.
 * When loaded it will return the first advancement that the conditions pass.
 *
 * This allows for multiple variants of an advancement to share the same name in the registry. Which allows dependents
 * to reference it without having to care about the conditions themselves.
 *
 * This is most likely useful when you have variants of a recipe based on what mods/resources are installed but want
 * to maintain the same 'entry' in the advancement book.
 */
public class ConditionalAdvancement {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private static final ResourceLocation DOESNT_MATTER = new ResourceLocation("doesnt", "matter");

        private List<Pair> advancements = new ArrayList<>();
        private ICondition condition;

        public Builder condition(ICondition value) {
            this.condition = value;
            return this;
        }

        public Builder advancement(Consumer<Consumer<Advancement.Builder>> callable) {
            callable.accept(this::advancement);
            return this;
        }

        public Builder advancement(Advancement.Builder builder) {
            return advancement(builder.build(DOESNT_MATTER).value());
        }

        public Builder advancement(AdvancementHolder holder) {
            return advancement(holder.value());
        }

        private Builder advancement(Advancement value) {
            if (condition == null)
                throw new IllegalStateException("Can not add a advancement with no conditions.");

            if (value == null)
                throw new IllegalStateException("Can not add a null advancement");

            this.advancements.add(new Pair(this.condition, value));
            this.condition = null;

            return this;
        }

        public JsonObject build() {
            var json = new JsonObject();
            var array = new JsonArray();
            json.add("advancements", array);

            for (var pair : advancements) {
                JsonObject holder = (JsonObject)Util.getOrThrow(Advancement.CODEC.encodeStart(JsonOps.INSTANCE, pair.adv()), IllegalStateException::new);
                if (holder.has(ICondition.DEFAULT_FIELD))
                    throw new IllegalStateException("Recipe already serialized conditions!");
                ForgeHooks.writeCondition(pair.condition(), holder);

                array.add(holder);
            }
            return json;
        }
    }

    private record Pair(ICondition condition, Advancement adv) {}
}
