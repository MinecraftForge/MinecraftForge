package net.minecraftforge.client.extensions.common;

import net.minecraftforge.client.IItemDecorator;

import java.util.ArrayList;
import java.util.List;

public class ClientItemExtensionsImpl implements IClientItemExtensions
{
    private final List<IItemDecorator> itemDecorators = new ArrayList<>();

    @Override
    public List<IItemDecorator> getItemDecorators()
    {
        return itemDecorators;
    }
}
