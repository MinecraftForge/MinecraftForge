package net.minecraftforge.event.entity.player;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemTooltipEvent extends PlayerEvent
{
    private final boolean showAdvancedItemTooltips;
    private final ItemStack itemStack;
    private final List<String> toolTip;

    /**
     * This event is fired in {@link ItemStack#getTooltip(EntityPlayer, boolean)}, which in turn is called from it's respective GUIContainer.
     */
    public ItemTooltipEvent(ItemStack itemStack, EntityPlayer entityPlayer, List<String> toolTip, boolean showAdvancedItemTooltips)
    {
        super(entityPlayer);
        this.itemStack = itemStack;
        this.toolTip = toolTip;
        this.showAdvancedItemTooltips = showAdvancedItemTooltips;
    }

    /**
     * Whether the advanced information on item tooltips is being shown, toggled by F3+H.
     */
    public boolean isShowAdvancedItemTooltips()
    {
        return showAdvancedItemTooltips;
    }

    /**
     * The {@link ItemStack} with the tooltip.
     */
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    /**
     * The {@link ItemStack} tooltip.
     */
    public List<String> getToolTip()
    {
        return toolTip;
    }
}