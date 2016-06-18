package net.minecraftforge;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.IFixableData;

final class ForgeDataFixer implements IDataFixer
{

    private final IDataFixer vanillaFixer;
    private final IFixableData[] fixersToRun;

    public ForgeDataFixer(DataFixer vanillaFixer, IFixableData[] fixersToRun)
    {
        this.vanillaFixer = vanillaFixer;
        this.fixersToRun = fixersToRun;
    }

    @Override
    public NBTTagCompound process(IFixType type, NBTTagCompound compound, int versionIn)
    {
        compound = vanillaFixer.process(type, compound, versionIn);
        for (IFixableData fixer : fixersToRun)
        {
            compound = fixer.fixTagCompound(compound);
        }
        return compound;
    }
}
