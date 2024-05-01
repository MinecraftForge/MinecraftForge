/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;

public final class TrueCondition implements ICondition
{
    public static final TrueCondition INSTANCE = new TrueCondition();
    private static final ResourceLocation NAME = new ResourceLocation("forge", "true");

    private TrueCondition() {}

    @Override
    public ResourceLocation getID()
    {
        return NAME;
    }

    @Override
    public boolean test()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "true";
    }

    public static class Serializer implements IConditionSerializer<TrueCondition>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, TrueCondition value) { }

        @Override
        public TrueCondition read(JsonObject json)
        {
            return TrueCondition.INSTANCE;
        }

        @Override
        public ResourceLocation getID()
        {
            return TrueCondition.NAME;
        }
    }
}
