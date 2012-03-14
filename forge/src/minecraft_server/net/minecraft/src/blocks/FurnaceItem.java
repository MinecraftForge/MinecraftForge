package net.minecraft.src.blocks;

import net.minecraft.src.*;

public class FurnaceItem extends ItemBlock
{
    public static final String blockType[] =
    {
        "iron", "brick", "sandstone", "obsidian", "redstone", "netherrack", 
        "stonebrick", "endstone", "glowstone"
    };

    public FurnaceItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        //MinecraftForgeClient.registerCustomItemRenderer(mod_InfiBlocks.furnace.blockID, this);
    }

    @Override
    public int getMetadata(int md)
    {
        return md;
    }
}
