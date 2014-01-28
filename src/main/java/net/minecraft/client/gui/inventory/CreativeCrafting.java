package net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class CreativeCrafting implements ICrafting
{
    private final Minecraft field_146109_a;
    private static final String __OBFID = "CL_00000751";

    public CreativeCrafting(Minecraft par1Minecraft)
    {
        this.field_146109_a = par1Minecraft;
    }

    public void sendContainerAndContentsToPlayer(Container par1Container, List par2List) {}

    // JAVADOC METHOD $$ func_71111_a
    public void sendSlotContents(Container par1Container, int par2, ItemStack par3ItemStack)
    {
        this.field_146109_a.playerController.sendSlotPacket(par3ItemStack, par2);
    }

    // JAVADOC METHOD $$ func_71112_a
    public void sendProgressBarUpdate(Container par1Container, int par2, int par3) {}
}