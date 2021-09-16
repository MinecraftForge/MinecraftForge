package net.minecraftforge.items;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ItemDecorator;

import javax.annotation.Nullable;
import java.util.*;

public class ItemDecoratorHandler implements IItemDecoratorHandler
{
    private ArrayList<ItemDecorator> ItemDecoratorCache = new ArrayList<>();
    private static final String DECORATORS = "CapabilityItemDecoratorHandler";
    
    @Override
    public void addDecorator(ItemDecorator decorator)
    {
        ItemDecoratorCache.add(decorator);
    }

    @Override
    public void removeDecorator(ItemDecorator decorator)
    {
        for (ItemDecorator itemDecorator: ItemDecoratorCache) {
            if(itemDecorator.equals(decorator))
            {
                ItemDecoratorCache.remove(decorator);
            }
        }
    }

    @Override
    public void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel)
    {
        for (ItemDecorator itemDecorator: ItemDecoratorCache) {
            itemDecorator.render(font, stack, xOffset, yOffset, itemCountLabel);
        }
    }
}
