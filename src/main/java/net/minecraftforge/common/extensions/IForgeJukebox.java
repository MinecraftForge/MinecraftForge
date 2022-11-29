package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface IForgeJukebox
{
    /**
     * Fires when an entity inserting an {@link net.minecraft.world.item.ItemStack} into a jukebox.
     * @param entity The entity inserting into jukebox. This can be null.
     * @param level The level of the jukebox.
     * @param pos The position of the jukebox.
     * @param state The blockstate of the jukebox
     * @param stack The itemstack being inserted.
     */
    void setRecord(@Nullable Entity entity, LevelAccessor level, BlockPos pos, BlockState state, ItemStack stack);
}
