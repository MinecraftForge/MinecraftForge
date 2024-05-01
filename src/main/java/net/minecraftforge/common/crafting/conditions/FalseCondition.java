/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public final class FalseCondition implements ICondition
{
    public static final FalseCondition INSTANCE = new FalseCondition();
    private static final ResourceLocation NAME = new ResourceLocation("forge", "false");

    private FalseCondition() {}

    @Override
    public ResourceLocation getID()
    {
        return NAME;
    }

    @Override
    public boolean test()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "false";
    }

    public static class Serializer implements IConditionSerializer<FalseCondition>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, FalseCondition value) { }

        @Override
        public FalseCondition read(JsonObject json)
        {
            return FalseCondition.INSTANCE;
        }

        @Override
        public ResourceLocation getID()
        {
            return FalseCondition.NAME;
        }
    }
}
