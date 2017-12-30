package net.minecraftforge.items.itemhandlerobserver;


public interface IItemHandlerObservable
{
    /**
     * @param observer adds a observer to this
     * @return true when the observer is added
     */
    boolean addObserver(IItemHandlerObserver observer);

    void removeObserver(IItemHandlerObserver observer);
}
