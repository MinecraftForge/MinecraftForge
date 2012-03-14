package net.minecraft.src.flora;

import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.mod_FloraSoma;
import net.minecraft.src.forge.MinecraftForgeClient;

public class RedwoodItem extends ItemBlock
{
    public static final String blockType[] =
    {
        "bark", "heart", "planks", "bloodbark", "bloodplanks", "sakurabark", "sakuraplanks", 
        "eucalyptusbark", "eucalyptusplanks"
    };

    public RedwoodItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        //MinecraftForgeClient.registerCustomItemRenderer(mod_FloraSoma.redwood.blockID, this);
    }
    
    @Override
    public int getMetadata(int md)
    {
        return md;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("Redwood").toString();
    }
}
