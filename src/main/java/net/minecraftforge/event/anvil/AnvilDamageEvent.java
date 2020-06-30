package net.minecraftforge.event.anvil;

import net.minecraft.block.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Cancelable
public class AnvilDamageEvent extends Event
{
    /**
     * A simple event fired whenever code is fired from the FallingBlockEntity to damage the Anvil.
     * If the event is canceled, vanilla behaviour will not run and the Anvil's BlockState will not change (It won't be damaged).
     * If the event is not canceled, vanilla behaviour will run and the Anvil will be damaged.
     */
    public static class Falling extends AnvilDamageEvent
    {
        /**
         * The current blockstate beneath the FallingBlockEntity for the Anvil.
         */
        @Nullable
        private final BlockState groundState;

        public Falling(@Nonnull BlockState currentState, @Nullable BlockState groundState)
        {
            super(currentState);
            this.groundState = groundState;
        }

        @Nullable
        public BlockState getGroundState() { return groundState; }
    }

    /**
     * The current state of the Anvil pre-damage
     */
    @Nonnull
    private final BlockState currentState;

    /**
     * A simple event fired whenever code is fired to attempt to damage the Anvil.
     * If the event is canceled, vanilla behaviour will not run and the Anvil's BlockState will not change (It won't be damaged).
     * If the event is not canceled, vanilla behaviour will run and the Anvil will be damaged.
     */
    public AnvilDamageEvent(@Nonnull BlockState currentState)
    {
        this.currentState = currentState;
    }

    @Nonnull
    public BlockState getCurrentState() { return currentState; }
}
