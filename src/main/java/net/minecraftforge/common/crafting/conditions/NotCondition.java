/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

public class NotCondition implements ICondition
{
    private static final ResourceLocation NAME = new ResourceLocation("forge", "not");
    private final ICondition child;

    public NotCondition(ICondition child)
    {
        this.child = child;
    }

    @Override
    public ResourceLocation getID()
    {
        return NAME;
    }

    @Override
    public boolean test()
    {
        return test(IContext.EMPTY);
    }

    @Override
    public boolean test(IContext context)
    {
        return !child.test(context);
    }

    @Override
    public String toString()
    {
        return "!" + child;
    }

    public static class Serializer implements IConditionSerializer<NotCondition>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, NotCondition value)
        {
            json.add("value", CraftingHelper.serialize(value.child));
        }

        @Override
        public NotCondition read(JsonObject json)
        {
            return new NotCondition(CraftingHelper.getCondition(GsonHelper.getAsJsonObject(json, "value")));
        }

        @Override
        public ResourceLocation getID()
        {
            return NotCondition.NAME;
        }
    }
}
