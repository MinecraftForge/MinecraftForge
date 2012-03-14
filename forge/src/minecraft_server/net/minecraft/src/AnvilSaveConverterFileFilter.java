package net.minecraft.src;

import java.io.File;
import java.io.FilenameFilter;

class AnvilSaveConverterFileFilter implements FilenameFilter
{
    final AnvilSaveConverter field_48645_a;

    AnvilSaveConverterFileFilter(AnvilSaveConverter par1AnvilSaveConverter)
    {
        this.field_48645_a = par1AnvilSaveConverter;
    }

    public boolean accept(File par1File, String par2Str)
    {
        return par2Str.endsWith(".mcr");
    }
}
