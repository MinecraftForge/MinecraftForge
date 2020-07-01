package net.minecraftforge.event.anvil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A simple event fired whenever code is fired to attempt to damage the Anvil.
 * If the event is canceled, vanilla behaviour will not run and the Anvil's BlockState will not change (It won't be damaged).
 * If the event is not canceled, vanilla behaviour will run and the Anvil will be damaged.
 */
@Cancelable
public class AnvilDamageEvent extends Event
{
    @Nonnull
    private final BlockState currentState;

    public AnvilDamageEvent(@Nonnull BlockState currentState)
    {
        this.currentState = currentState;
    }

    /**
     * @return Returns the Current BlockState of the Anvil Pre-Damage.
     */
    @Nonnull
    public BlockState getCurrentState() { return currentState; }

    /**
     * A simple event fired whenever code is fired from the FallingBlockEntity to damage the Anvil.
     * If the event is canceled, vanilla behaviour will not run and the Anvil's BlockState will not change (It won't be damaged).
     * If the event is not canceled, vanilla behaviour will run and the Anvil will be damaged.
     */
    public static class Falling extends AnvilDamageEvent
    {
        @Nullable
        private final BlockState groundState;
        @Nonnull
        private final FallingBlockEntity fallingBlockEntity;
        @Nonnull
        private final World world;

        public Falling(@Nonnull BlockState currentState, @Nonnull BlockPos fallingEntityPos, @Nullable BlockState groundState, @Nonnull BlockPos groundStatePos, @Nonnull FallingBlockEntity fallingBlockEntity, @Nonnull World world)
        {
            super(currentState);
            this.groundState = groundState;
            this.fallingBlockEntity = fallingBlockEntity;
            this.world = world;
        }

        /**
         * @return Returns the state below the FallingBlockEntity.
         */
        @Nullable
        public BlockState getGroundState() { return groundState; }

        /**
         * @return Returns the FallingBlockEntity itself.
         */
        @Nonnull
        public FallingBlockEntity getFallingBlockEntity() { return fallingBlockEntity; }

        /**
         * @return Returns the World of the FallingBlockEntity.
         */
        @Nonnull
        public World getWorld() { return world; }
    }

    /**
     * A simple event fired whenever code is fired from the RepairContainer to damage the Anvil.
     * If the event is canceled, vanilla behaviour will not run and the Anvil's BlockState will not change (It won't be damaged).
     * If the event is not canceled, vanilla behaviour will run and the Anvil will be damaged.
     */
    public static class Container extends AnvilDamageEvent
    {
        @Nonnull
        private final PlayerEntity player;
        @Nonnull
        private final World world;
        @Nonnull
        private final RepairContainer repairContainer;

        public Container(@Nonnull BlockState currentState, @Nonnull PlayerEntity player, @Nonnull World world, @Nonnull RepairContainer repairContainer)
        {
            super(currentState);
            this.player = player;
            this.world = world;
            this.repairContainer = repairContainer;
        }

        /**
         * @return Returns the PlayerEntity using the RepairContainer.
         */
        @Nonnull
        public PlayerEntity getPlayer() { return player; }

        /**
         * @return Returns the World of the PlayerEntity using the RepairContainer.
         */
        @Nonnull
        public World getWorld() { return world; }

        /**
         * @return Returns the RepairContainer.
         */
        @Nonnull
        public RepairContainer getRepairContainer() {
            return repairContainer;
        }
    }
}