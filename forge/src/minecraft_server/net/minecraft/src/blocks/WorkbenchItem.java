package net.minecraft.src.blocks;

import net.minecraft.src.*;

public class WorkbenchItem extends ItemBlock
{
    public static final String blockType[] =
    {
        "cobblestone", "iron", "redstone", "bone", "sandstone", "lapis", "obsidian",
        "cactus", "netherrack", "ice", "stoneBrick"
    };

    public WorkbenchItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        ///MinecraftForgeClient.registerCustomItemRenderer(mod_InfiBlocks.workbench.blockID, this);
    }
    
    public int getMetadata(int i)
    {
        return i;
    }

    public int getIconFromDamage(int i)
    {
        return mod_InfiBlocks.workbench.getBlockTextureFromSideAndMetadata(0, i);
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("Workbench").toString();
    }
}
