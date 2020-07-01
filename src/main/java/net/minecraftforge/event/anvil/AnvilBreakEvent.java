package net.minecraftforge.event.anvil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Cancelable
public class AnvilBreakEvent extends Event
{
    @Nonnull
    private final BlockState statePreBreaking;
    @Nonnull
    private final World world;
    @Nonnull
    private final BlockPos pos;
    @Nullable
    private final FallingBlockEntity fallingBlockEntity;
    @Nullable
    private final PlayerEntity playerEntity;

    public AnvilBreakEvent(@Nonnull BlockState statePreBreaking, @Nonnull World world, @Nonnull BlockPos pos, @Nullable FallingBlockEntity fallingBlockEntity, @Nullable PlayerEntity playerEntity)
    {
        this.statePreBreaking = statePreBreaking;
        this.world = world;
        this.pos = pos;
        this.fallingBlockEntity = fallingBlockEntity;
        this.playerEntity = playerEntity;
    }

    /**
     * @return Returns the BlockState before it broke.
     */
    @Nonnull
    public BlockState getStatePreBreaking() { return statePreBreaking; }

    /**
     * This is either supplied by the FallingBlockEntity or the PlayerEntity.
     * @return Returns the World where the breaking took place.
     */
    @Nonnull
    public World getWorld() { return world; }

    /**
     * @return Returns the position of where the Break took place.
     */
    @Nonnull
    public BlockPos getPos() { return pos; }

    /**
     * @return Returns the FallingBlockEntity, This can return Null!
     */
    @Nullable
    public FallingBlockEntity getFallingBlockEntity() { return fallingBlockEntity; }

    /**
     * @return Returns the PlayerEntity, This can return Null!
     */
    @Nullable
    public PlayerEntity getPlayerEntity() { return playerEntity; }
}