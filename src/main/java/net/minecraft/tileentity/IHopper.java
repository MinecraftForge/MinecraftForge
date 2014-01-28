package net.minecraft.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

public interface IHopper extends IInventory
{
    World func_145831_w();

    // JAVADOC METHOD $$ func_96107_aA
    double getXPos();

    // JAVADOC METHOD $$ func_96109_aB
    double getYPos();

    // JAVADOC METHOD $$ func_96108_aC
    double getZPos();
}