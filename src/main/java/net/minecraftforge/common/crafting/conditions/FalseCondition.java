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

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

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
