package net.minecraftforge.common.util;

import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.text.TextComponentBase;

public abstract class TextComponentSerializable extends TextComponentBase implements IJsonSerializable
{
    public String getFallbackText() { return "{Unsupported component type: "+getClass().getName()+"}"; }
}
