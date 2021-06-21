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

package net.minecraftforge.entity.attributes;

import net.minecraft.entity.ai.attributes.RangedAttribute;

public class BooleanAttribute extends RangedAttribute
{

    public BooleanAttribute(String descriptionId, boolean enabledByDefault)
    {
        super(descriptionId, enabledByDefault ? 1 : 0, 0, 1);
    }

    @Override
    public double sanitizeValue(double valueIn)
    {
        return valueIn > 0 ? 1 : 0;
    }

    @Override
    public boolean isBooleanAttribute() {
        return true;
    }

    /*
    To reduce headache, every one of these calculations is treated as addition.

    That is:
        If the modifier has a value greater than 0, a value of 1 is added to the current value of the attribute.
        If the modifier has a value less than 0, a value of 1 is subtracted from the current value of the attribute.
        If the modifier has a value of 0, the current value of the attribute is unchanged.

    This means that any two opposing modifier values cancel each other out.
     */

    @Override
    public double calculateAddition(double currentValueIn, double modifierValueIn)
    {
        return currentValueIn + (modifierValueIn > 0 ? 1 : modifierValueIn < 0 ? -1 : 0);
    }

    @Override
    public double calculateMultiplyBase(double preMultiplyValueIn, double currentValueIn, double modifierValueIn)
    {
        return currentValueIn + (modifierValueIn > 0 ? 1 : modifierValueIn < 0 ? -1 : 0);
    }

    @Override
    public double calculateMultiplyTotal(double preMultiplyValueIn, double currentValueIn, double modifierValueIn)
    {
        return currentValueIn + (modifierValueIn > 0 ? 1 : modifierValueIn < 0 ? -1 : 0);
    }
}
