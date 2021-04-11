package net.minecraftforge.common.extensions;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Direction;

public interface IForgeSpawnEggItem
{
    default net.minecraft.dispenser.IDispenseItemBehavior createDispenseBehaviour()
    {
        return new net.minecraft.dispenser.DefaultDispenseItemBehavior()
        {
            public ItemStack execute(net.minecraft.dispenser.IBlockSource source, ItemStack stack)
            {
                Direction facing = source.getBlockState().getValue(net.minecraft.block.DispenserBlock.FACING);
                EntityType<?> entitytype = ((SpawnEggItem)stack.getItem()).getType(stack.getTag());

                entitytype.spawn(source.getLevel(), stack, null, source.getPos().relative(facing), SpawnReason.DISPENSER, facing != Direction.UP, false);
                stack.shrink(1);
                return stack;
            }
        };
    }
}