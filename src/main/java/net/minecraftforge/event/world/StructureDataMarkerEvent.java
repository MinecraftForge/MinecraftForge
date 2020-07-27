package net.minecraftforge.event.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.Random;

/**
 * Fired before a {@link TemplateStructurePiece#handleDataMarker}
 * The {@link #function} is saved metadata of the marker
 * The marker is is saved at {@link #pos} and generated in {@link #world} using {@link #rand}
 * The generated structure is {@link #structurePiece}
 *
 * Cancelling the event will prevent the vanilla logic to be executed
 */
@Cancelable
public class StructureDataMarkerEvent extends Event
{

    private final String function;
    private final BlockPos pos;
    private final IWorld world;
    private final Random rand;
    private final MutableBoundingBox sbb;
    private final TemplateStructurePiece structurePiece;

    public StructureDataMarkerEvent(TemplateStructurePiece structurePiece, String function, BlockPos pos, IWorld world, Random rand, MutableBoundingBox sbb)
    {
        this.function = function;
        this.pos = pos;
        this.world = world;
        this.rand = rand;
        this.sbb = sbb;
        this.structurePiece = structurePiece;
    }


    /**
     * Get the bounding box
     *
     * @return the piece bounding box
     */
    public MutableBoundingBox getBoundingBox()
    {
        return sbb;
    }

    /**
     * Get the generated piece instance
     *
     * @return the generated piece instance
     */
    public TemplateStructurePiece getStructurePiece()
    {
        return structurePiece;
    }


    /**
     * The used random
     *
     * @return the random
     */
    public Random getRandom()
    {
        return rand;
    }


    /**
     * Get the world
     *
     * @return the world
     */
    public IWorld getWorld()
    {
        return world;
    }


    /**
     * Get position
     *
     * @return the position
     */
    public BlockPos getPos()
    {
        return pos;
    }


    /**
     * Get metadata
     *
     * @return the metadata
     */
    public String getFunction()
    {
        return function;
    }
}
