package net.minecraftforge.entity.attributes;

import net.minecraft.entity.ai.attributes.RangedAttribute;

public class BooleanAttribute extends RangedAttribute {

    public BooleanAttribute(String descriptionId, boolean enabledByDefault) {
        super(descriptionId, enabledByDefault ? 1 : 0, 0, 1);
    }

    @Override
    public double sanitizeValue(double valueIn) {
        return valueIn > 0 ? 1 : 0;
    }
}
