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

    @Override
    public boolean containsObserver(IItemHandlerObserver observer)
    {
        return observers.contains(observer);
    }


    public void onStackInserted(IItemHandler handler, int slot, @Nonnull ItemStack newStack)
    {
        cleanup();
        observers.forEach(observer -> observer.onStackInserted(handler, slot, newStack));
    }


    public void onStackExtracted(IItemHandler handler, int slot, @Nonnull ItemStack newStack)
    {
        cleanup();
        observers.forEach(observer -> observer.onStackExtracted(handler, slot, newStack));
    }

    public void onObserverableInvalidated()
    {
        observers.forEach(IItemHandlerObserver::onObserverableInvalidated);
    }

    public void cleanup()
    {
        observers.removeIf(IItemHandlerObserver::isNotValid);
    }
}
