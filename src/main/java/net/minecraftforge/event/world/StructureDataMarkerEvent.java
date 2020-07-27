package net.minecraftforge.event.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.Random;

@Cancelable
public class StructureDataMarkerEvent extends Event {

    public final String function;
    public final BlockPos pos;
    public final IWorld world;
    public final Random rand;
    public final MutableBoundingBox sbb;
    public final TemplateStructurePiece structurePiece;

    public StructureDataMarkerEvent(TemplateStructurePiece structurePiece, String function, BlockPos pos, IWorld world, Random rand, MutableBoundingBox sbb) {
        this.function = function;
        this.pos = pos;
        this.world = world;
        this.rand = rand;
        this.sbb = sbb;
        this.structurePiece = structurePiece;
    }
}
