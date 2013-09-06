package net.minecraftforge.object;

/**
  A interface which appears on anything that can be stored in an inventory.
  This includes blocks, items and fluids.
  @author gjgfuj
  */
public interface IInventoryObject
{
  /**
    @return The block or item id which represents the part
    */
  public int getID();
}