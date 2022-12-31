package net.minecraftforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class AlterGroundEvent extends Event {
    private final LevelSimulatedReader level;
    private final RandomSource random;
    private final BlockPos pos;
    private BlockState altered;

    public AlterGroundEvent(LevelSimulatedReader level, RandomSource random, BlockPos pos, BlockState altered) {
        super();
        this.level = level;
        this.random = random;
        this.pos = pos;
        this.altered = altered;
    }

    public LevelSimulatedReader getLevel() {
        return this.level;
    }

    public RandomSource getRandom() {
        return this.random;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getAlteredState() {
        return this.altered;
    }

    public void setAlteredState(BlockState altered) {
        this.altered = altered;
    }
}
