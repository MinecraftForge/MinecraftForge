package net.minecraft.entity.ai.attributes;

public interface IAttribute
{
    String getAttributeUnlocalizedName();

    double clampValue(double var1);

    double getDefaultValue();

    boolean getShouldWatch();
}