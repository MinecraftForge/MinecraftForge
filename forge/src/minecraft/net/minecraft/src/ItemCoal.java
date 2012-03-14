package net.minecraft.src;

public class ItemCoal extends Item
{
    public ItemCoal(int par1)
    {
        super(par1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    public String getItemNameIS(ItemStack par1ItemStack)
    {
        return par1ItemStack.getItemDamage() == 1 ? "item.charcoal" : "item.coal";
    }
}
