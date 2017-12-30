package net.minecraftforge.items.itemhandlerobserver;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class DefaultObservable implements IItemHandlerObservable
{
    private final List<IItemHandlerObserver> observers = new ArrayList<>();

    @Override
    public boolean addObserver(IItemHandlerObserver observer)
    {
        return observers.add(observer);
    }

    @Override
    public void removeObserver(IItemHandlerObserver observer)
    {
        observers.remove(observer);
    }

    public void onStackInserted(IItemHandler handler, int slot, @Nonnull ItemStack oldStack, @Nonnull ItemStack newStack)
    {
        observers.forEach(observer -> observer.onStackInserted(handler, slot, oldStack, newStack));
    }

    public void onStackExtracted(IItemHandler handler, int slot, @Nonnull ItemStack oldStack, @Nonnull ItemStack newStack)
    {
        observers.forEach(observer -> observer.onStackExtracted(handler, slot, oldStack, newStack));
    }

    public void onInvalidated()
    {
        observers.forEach(IItemHandlerObserver::onInvalidated);
    }
}
