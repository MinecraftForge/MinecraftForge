package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface ICrafting
{
    void sendContainerAndContentsToPlayer(Container var1, List var2);

    // JAVADOC METHOD $$ func_71111_a
    void sendSlotContents(Container var1, int var2, ItemStack var3);

    // JAVADOC METHOD $$ func_71112_a
    void sendProgressBarUpdate(Container var1, int var2, int var3);
}