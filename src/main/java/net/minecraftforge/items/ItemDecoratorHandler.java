package net.minecraftforge.items;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ItemDecorator;

import javax.annotation.Nullable;
import java.util.*;

public class ItemDecoratorHandler implements IItemDecoratorHandler
{
    private Map<String, ItemDecorator> ItemDecoratorCache = new HashMap();
    private static final String DECORATORS = "CapabilityItemDecoratorHandler";
    
    @Override
    public void addDecorator(ItemDecorator decorator)
    {
        ItemDecoratorCache.put(decorator.getKey().toString(), decorator);
    }

    @Override
    public void removeDecorator(ItemDecorator decorator)
    {
        ItemDecoratorCache.remove(decorator.getKey().toString());
    }

    @Override
    public void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel)
    {
        for (ItemDecorator itemDecorator: ItemDecoratorCache.values()) {
            itemDecorator.render(font, stack, xOffset, yOffset, itemCountLabel);
        }
    }
}
