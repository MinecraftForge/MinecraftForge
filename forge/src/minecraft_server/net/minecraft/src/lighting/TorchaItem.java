package net.minecraft.src.lighting;

import net.minecraft.src.*;

public class TorchaItem extends ItemBlock
{

    public TorchaItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getIconFromDamage(int i)
    {
        return mod_InfiLighting.torcha.getBlockTextureFromSideAndMetadata(0, i);
    }

    public int getMetadata(int i)
    {
        return i;
    }
}
