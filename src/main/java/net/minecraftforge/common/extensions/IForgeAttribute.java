package net.minecraftforge.common.extensions;

import net.minecraft.entity.ai.attributes.Attribute;

public interface IForgeAttribute
{

    default Attribute getAttribute()
    {
        return (Attribute)this;
    }

    /**
     * Determines whether or not this should be considered a boolean attribute
     * Used by ItemStack#getTooltipLines and PotionUtils#addPotionTooltip to display boolean attribute modifier information to the client
     * @return Whether or not this attribute is a boolean one
     */
    default boolean isBooleanAttribute()
    {
        return false;
    }

    /**
     * Used in ModifiableAttributeInstance#calculateValue for handling AttributeModifier.Operation.ADDITION operations
     * Vanilla adds the modifier value to the current value for each ADDITION AttributeModifier
     * Override this for your custom Attribute implementation to change how ADDITION is handled for it
     * @param currentValueIn The current, non-sanitized value of the ModifiableAttributeInstance, during ADDITION operations
     * @param modifierValueIn The value of an AttributeModifier
     * @return The ADDITION value calculated for the Attribute
     */
    default double calculateAddition(double currentValueIn, double modifierValueIn)
    {
        return currentValueIn + modifierValueIn;
    }

    /**
     * Used in ModifiableAttributeInstance#calculateValue for handling AttributeModifier.Operation.MULTIPLY_BASE operations
     * Vanilla adds (the pre-multiply value * the modifier value) to the current value for each MULTIPLY_BASE AttributeModifier
     * Override this for your custom Attribute implementation to change how MULTIPLY_BASE is handled for it
     * @param preMultiplyValueIn The value of the ModifiableAttributeInstance, after all ADDITION operations but before any MULTIPLY operations
     * @param currentValueIn The current, non-sanitized value of the ModifiableAttributeInstance
     * @param modifierValueIn The value of an AttributeModifier
     * @return The MULTIPLY_BASE value calculated for the Attribute
     */
    default double calculateMultiplyBase(double preMultiplyValueIn, double currentValueIn, double modifierValueIn)
    {
        return currentValueIn + (preMultiplyValueIn * modifierValueIn);
    }

    /**
     * Used in ModifiableAttributeInstance#calculateValue for handling AttributeModifier.Operation.MULTIPLY_TOTAL operations
     * Vanilla multiplies the current value by (1 + modifier value) for each MULTIPLY_TOTAL AttributeModifier
     * Override this for your custom Attribute implementation to change how MULTIPLY_TOTAL is handled for it
     * @param preMultiplyValueIn The value of the ModifiableAttributeInstance, after all ADDITION operations but before any MULTIPLY operations
     * @param currentValueIn The current, non-sanitized value of the ModifiableAttributeInstance
     * @param modifierValueIn The value of an AttributeModifier
     * @return The MULTIPLY_TOTAL value calculated for the Attribute
     */
    default double calculateMultiplyTotal(double preMultiplyValueIn, double currentValueIn, double modifierValueIn)
    {
        return currentValueIn * (1.0D + modifierValueIn);
    }
}
