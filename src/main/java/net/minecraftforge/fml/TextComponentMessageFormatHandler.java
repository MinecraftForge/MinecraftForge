package net.minecraftforge.fml;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class TextComponentMessageFormatHandler {
    public static int handle(final TextComponentTranslation parent, final List<ITextComponent> children, final Object[] formatArgs, final String format) {
        try {
            children.add(new TextComponentString(ForgeI18n.parseFormat(format, formatArgs)));
            return format.length();
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }
}
