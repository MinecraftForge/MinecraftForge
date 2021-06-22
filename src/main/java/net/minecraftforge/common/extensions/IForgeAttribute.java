package net.minecraftforge.common.extensions;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.List;

public interface IForgeAttribute
{

    default Attribute getAttribute()
    {
        return (Attribute)this;
    }

    /**
     * Determines whether or not this should be considered a custom attribute
     * Used by ItemStack#getTooltipLines and PotionUtils#addPotionTooltip to display custom attribute modifier information to the client
     * @return Whether or not this attribute is a custom one
     */
    default boolean isCustom()
    {
        return false;
    }

    /**
     * Called in ItemStack#getTooltipLines when this Attribute is considered a custom attribute by overriding isCustom to return true.
     * Override this to handle custom tooltip logic for displaying attribute modifier information to the client for an ItemStack.
     * @param stack The ItemStack to handle the tooltip for
     * @param player The Player holding the ItemStack, if not null. Note that this would be the client since this method is called client-side.
     * @param tooltipList The current List of tooltips. This is what you should be modifying.
     * @param modifierMultimap The Multimap of Attribute-AttributeModifier for the ItemStack for a given EquipmentSlotType
     * @param modifier The AttributeModifier to add a tooltip for
     * @param slotType The EquipmentSlotType used to obtain the Attribute-AttributeModifier Multimap from the ItemStack
     */
    default void addCustomTooltipToItemStack(ItemStack stack, @Nullable PlayerEntity player, List<ITextComponent> tooltipList, Multimap<Attribute, AttributeModifier> modifierMultimap, AttributeModifier modifier, EquipmentSlotType slotType)
    {

    }

    /**
     * Called in PotionUtils#addPotionTooltip when this Attribute is considered a custom attribute by overriding isCustom to return true.
     * Override this to handle custom tooltip logic for displaying attribute modifier information to the client for an ItemStack containing a Potion.
     * @param stack The ItemStack containing a Potion to handle the tooltip for
     * @param player The Player holding the ItemStack, if not null. Note that this would be the client since this method is called client-side.
     * @param modifierList The List of Attribute-AttributeModifier pairings for the ItemStack containing a Potion
     * @param tooltipList The current List of tooltips. This is what you should be modifying.
     * @param modifier The AttributeModifier to add a tooltip for
     */
    default void addCustomTooltipToPotion(ItemStack stack, @Nullable PlayerEntity player, List<ITextComponent> tooltipList, List<Pair<Attribute, AttributeModifier>> modifierList, AttributeModifier modifier)
    {

    }

    /**
     * Used in ModifiableAttributeInstance#calculateValue for handling AttributeModifier.Operation.ADDITION operations
     * Vanilla adds the modifier value to the current value for each ADDITION AttributeModifier
     * Override this for your custom Attribute implementation to change how ADDITION is handled for it
     * @param currentValueIn The current, non-sanitized value of the ModifiableAttributeInstance, during ADDITION operations
     * @param modifierValueIn The value of an AttributeModifier
     * @return The ADDITION value calculated for the Attribute
     */
    default double calculateAdditionAndUpdateCurrent(double currentValueIn, double modifierValueIn)
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
    default double calculateMultiplyBaseAndUpdateCurrent(double preMultiplyValueIn, double currentValueIn, double modifierValueIn)
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
    default double calculateMultiplyTotalAndUpdateCurrent(double preMultiplyValueIn, double currentValueIn, double modifierValueIn)
    {
        return currentValueIn * (1.0D + modifierValueIn);
    }
}
