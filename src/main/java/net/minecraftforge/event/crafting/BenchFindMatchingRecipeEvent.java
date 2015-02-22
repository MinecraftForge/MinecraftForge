package net.minecraftforge.event.crafting;

import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BenchFindMatchingRecipeEvent extends FindMatchingRecipeEvent.Block {
    public final ContainerWorkbench bench;

    public BenchFindMatchingRecipeEvent(ContainerWorkbench container, World world, ItemStack output, BlockPos pos)
    {
        super(world, pos, PlayerFindMatchingRecipeEvent.getInput(container.craftMatrix), output, EType.CRAFT);
        bench = container;
    }
}
