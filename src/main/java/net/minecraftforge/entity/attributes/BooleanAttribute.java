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

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class BooleanAttribute extends Attribute
{

    public BooleanAttribute(String descriptionId, boolean enabledByDefault)
    {
        super(descriptionId, enabledByDefault ? 1 : 0);
    }

    @Override
    public boolean isCustom() {
        return true;
    }

    /**
     * If the stack has an ADDITION or MULTIPLY_BASE modifier, then the ItemStack's tooltip will either say it "Allows X" or "Denies X", where X is the action associated with the attribute
     * "Allows" means a provisional "yes" is given to performing the action
     * "Denies" means a provisional "no" is given to performing the action, which will cancel out a single "Allow"
     * If the stack has an MULTIPLY_TOTAL modifier with a value less than or equal to -1, then the tooltip will say it "Disables X", where X is the action associated with the attribute
     * "Disables" means a definitive "no" is given to performing the action, cancelling out any "Allows"
     * @param stack The ItemStack to handle the tooltip for
     * @param player The Player holding the ItemStack, if not null. Note that this would be the client since this method is called client-side.
     * @param tooltipList The current List of tooltips. This is what you should be modifying.
     * @param modifierMultimap The Multimap of Attribute-AttributeModifier for the ItemStack for a given EquipmentSlotType
     * @param modifier The AttributeModifier to add a tooltip for
     * @param slotType The EquipmentSlotType used to obtain the Attribute-AttributeModifier Multimap from the ItemStack
     */
    @Override
    public void addCustomTooltipToItemStack(ItemStack stack, @Nullable PlayerEntity player, List<ITextComponent> tooltipList, Multimap<Attribute, AttributeModifier> modifierMultimap, AttributeModifier modifier, EquipmentSlotType slotType)
    {
        double preMultiplyValue = this.getPreMultiplyValue(player, modifierMultimap);

        double modifierValue = modifier.getAmount();
        AttributeModifier.Operation operation = modifier.getOperation();
        if(operation != AttributeModifier.Operation.MULTIPLY_TOTAL){
            if(operation == AttributeModifier.Operation.ADDITION){
                if (modifierValue > 0.0D) {
                    tooltipList.add((new TranslationTextComponent("attribute.modifier.forge.allows", new TranslationTextComponent(this.getDescriptionId()))).withStyle(TextFormatting.BLUE));
                } else if (modifierValue < 0.0D) {
                    tooltipList.add((new TranslationTextComponent("attribute.modifier.forge.denies", new TranslationTextComponent(this.getDescriptionId()))).withStyle(TextFormatting.RED));
                }
            } else if(operation == AttributeModifier.Operation.MULTIPLY_BASE && player != null){
                if(preMultiplyValue * modifierValue > 0){
                    tooltipList.add((new TranslationTextComponent("attribute.modifier.forge.allows", new TranslationTextComponent(this.getDescriptionId()))).withStyle(TextFormatting.BLUE));
                } else if(preMultiplyValue * modifierValue < 0){
                    tooltipList.add((new TranslationTextComponent("attribute.modifier.forge.denies", new TranslationTextComponent(this.getDescriptionId()))).withStyle(TextFormatting.RED));
                }
            }
        } else if(1.0D + modifierValue <= 0) {
            tooltipList.add((new TranslationTextComponent("attribute.modifier.forge.disables", new TranslationTextComponent(this.getDescriptionId()))).withStyle(TextFormatting.DARK_RED));
        }
    }

    /**
     * If the stack has an ADDITION or MULTIPLY_BASE modifier, then the ItemStack's tooltip will either say it "Allows X" or "Denies X", where X is the action associated with the attribute
     * "Allows" means a provisional "yes" is given to performing the action
     * "Denies" means a provisional "no" is given to performing the action, which will cancel out a single "Allow"
     * If the stack has an MULTIPLY_TOTAL modifier with a value less than or equal to -1, then the tooltip will say it "Disables X", where X is the action associated with the attribute
     * "Disables" means a definitive "no" is given to performing the action, cancelling out any "Allows"
     * @param stack The ItemStack containing a Potion to handle the tooltip for
     * @param player The Player holding the ItemStack, if not null. Note that this would be the client since this method is called client-side.
     * @param tooltipList The current List of tooltips. This is what you should be modifying.
     * @param modifierList The List of Attribute-AttributeModifier pairings for the ItemStack containing a Potion
     * @param modifier The AttributeModifier to add a tooltip for
     */
    @Override
    public void addCustomTooltipToPotion(ItemStack stack, @Nullable PlayerEntity player, List<ITextComponent> tooltipList, List<Pair<Attribute, AttributeModifier>> modifierList, AttributeModifier modifier)
    {
        double preMultiplyValue = this.getPreMultiplyValue(player, modifierList);

        double modifierValue = modifier.getAmount();
        AttributeModifier.Operation operation = modifier.getOperation();
        if(operation != AttributeModifier.Operation.MULTIPLY_TOTAL){
            if(operation == AttributeModifier.Operation.ADDITION){
                if (modifierValue > 0.0D) {
                    tooltipList.add((new TranslationTextComponent("attribute.modifier.forge.allows", new TranslationTextComponent(this.getDescriptionId()))).withStyle(TextFormatting.BLUE));
                } else if (modifierValue < 0.0D) {
                    tooltipList.add((new TranslationTextComponent("attribute.modifier.forge.denies", new TranslationTextComponent(this.getDescriptionId()))).withStyle(TextFormatting.RED));
                }
            } else if(operation == AttributeModifier.Operation.MULTIPLY_BASE && player != null){
                if(preMultiplyValue * modifierValue > 0){
                    tooltipList.add((new TranslationTextComponent("attribute.modifier.forge.allows", new TranslationTextComponent(this.getDescriptionId()))).withStyle(TextFormatting.BLUE));
                } else if(preMultiplyValue * modifierValue < 0){
                    tooltipList.add((new TranslationTextComponent("attribute.modifier.forge.denies", new TranslationTextComponent(this.getDescriptionId()))).withStyle(TextFormatting.RED));
                }
            }
        } else if(1.0D + modifierValue <= 0) {
            tooltipList.add((new TranslationTextComponent("attribute.modifier.forge.disables", new TranslationTextComponent(this.getDescriptionId()))).withStyle(TextFormatting.DARK_RED));
        }
    }

    private double getPreMultiplyValue(@Nullable PlayerEntity player, Multimap<Attribute, AttributeModifier> modifierMultimap) {
        double preMultiplyValue = 0;
        if(player != null){
            preMultiplyValue = player.getAttributeBaseValue(this);
            if(!modifierMultimap.isEmpty()) {
                for (Map.Entry<Attribute, AttributeModifier> entry : modifierMultimap.entries()) {
                    if (entry.getKey() == this && entry.getValue().getOperation() == AttributeModifier.Operation.ADDITION) {
                        preMultiplyValue = this.calculateAdditionAndUpdateCurrent(preMultiplyValue, entry.getValue().getAmount());
                    }
                }
            }
        }
        return preMultiplyValue;
    }

    private double getPreMultiplyValue(@Nullable PlayerEntity player, List<Pair<Attribute, AttributeModifier>> modifierList) {
        double preMultiplyValue = 0;
        if(player != null){
            preMultiplyValue = player.getAttributeBaseValue(this);
            if(!modifierList.isEmpty()){
                for(Pair<Attribute, AttributeModifier> pair : modifierList) {
                    if (pair.getFirst() == this && pair.getSecond().getOperation() == AttributeModifier.Operation.ADDITION) {
                        preMultiplyValue = this.calculateAdditionAndUpdateCurrent(preMultiplyValue, pair.getSecond().getAmount());
                    }
                }
            }
        }
        return preMultiplyValue;
    }

    /**
     * We can only add 1 if modifierValueIn is greater than 0 or subtract 1 if modifierValueIn is less than 0
     * @param currentValueIn The current, non-sanitized value of the ModifiableAttributeInstance, during ADDITION operations
     * @param modifierValueIn The value of an AttributeModifier
     * @return The ADDITION value calculated for the Attribute
     */
    @Override
    public double calculateAdditionAndUpdateCurrent(double currentValueIn, double modifierValueIn)
    {
        return currentValueIn + (modifierValueIn > 0 ? 1 : modifierValueIn < 0 ? -1 : 0);
    }

    /**
     * We can only add 1 if (preMultiplyValueIn * modifierValueIn) is greater than 0 or subtract 1 if (preMultiplyValueIn + modifierValueIn) is less than 0
     * @param preMultiplyValueIn The value of the ModifiableAttributeInstance, after all ADDITION operations but before any MULTIPLY operations
     * @param currentValueIn The current, non-sanitized value of the ModifiableAttributeInstance
     * @param modifierValueIn The value of an AttributeModifier
     * @return The MULTIPLY_BASE value calculated for the Attribute
     */
    @Override
    public double calculateMultiplyBaseAndUpdateCurrent(double preMultiplyValueIn, double currentValueIn, double modifierValueIn)
    {
        return currentValueIn + (preMultiplyValueIn * modifierValueIn > 0 ? 1 : preMultiplyValueIn * modifierValueIn < 0 ? -1 : 0);
    }

    /**
     * The value is only changed if (1.0D + modifierValueIn) is less than 0
     * This function effectively gets the final say in whether or not this attribute is enabled due to potentially multiplying by 0
     * @param preMultiplyValueIn The value of the ModifiableAttributeInstance, after all ADDITION operations but before any MULTIPLY operations
     * @param currentValueIn The current, non-sanitized value of the ModifiableAttributeInstance
     * @param modifierValueIn The value of an AttributeModifier
     * @return The MULTIPLY_TOTAL value calculated for the Attribute
     */
    @Override
    public double calculateMultiplyTotalAndUpdateCurrent(double preMultiplyValueIn, double currentValueIn, double modifierValueIn)
    {
        return currentValueIn * (1.0D + modifierValueIn > 0 ? 1 : 0);
    }
}
