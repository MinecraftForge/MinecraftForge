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

package net.minecraftforge.common.crafting.conditions;

public interface IConditionBuilder
{
    default ICondition and(ICondition... values)
    {
        return new AndCondition(values);
    }

    default ICondition FALSE()
    {
        return FalseCondition.INSTANCE;
    }

    default ICondition TRUE()
    {
        return TrueCondition.INSTANCE;
    }

    default ICondition not(ICondition value)
    {
        return new NotCondition(value);
    }

    default ICondition or(ICondition... values)
    {
        return new OrCondition(values);
    }

    default ICondition itemExists(String namespace, String path)
    {
        return new ItemExistsCondition(namespace, path);
    }

    default ICondition modLoaded(String modid)
    {
        return new ModLoadedCondition(modid);
    }
}
