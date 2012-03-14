package net.minecraft.src.blocks;

import net.minecraft.src.*;
import net.minecraft.src.forge.MinecraftForgeClient;

public class InfiGlassPaneItem extends ItemBlock
{
	public static final String blockType[] =
	    {
			"clear", "soul", "clearSoul"
	    };

    public InfiGlassPaneItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
    }
    
    public int getMetadata(int i)
    {
        return i;
    }

    public int getIconFromDamage(int i)
    {
        return iconIndex + i;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("GlassPane").toString();
    }
}
