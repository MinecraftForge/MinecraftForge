package net.minecraftforge.object;

/**
  A interface which appears on anything that can be placed in the world.
  This includes blocks and fluids.
  @author gjgfuj
  */
public interface IPhysicalObject extends IInventoryObject
{
  /**
    @return The ID of the object when placed as a block.
    */
  public int getBlockID();
}