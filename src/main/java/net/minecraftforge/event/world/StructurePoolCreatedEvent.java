package net.minecraftforge.event.world;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.function.Function;

/**
 * StructurePoolCreatedEvent is fired when a new {@link net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool} is constructed.
 * When constructing a pool from a codec, this event isn't fired.
 * This allows users to add their own entries into the pool.
 */
public class StructurePoolCreatedEvent extends Event {

    private final ResourceLocation createdPoolName;
    private final List<Pair<StructurePoolElement, Integer>> rawTemplates;
    private final List<StructurePoolElement> templates;
    private final StructureTemplatePool.Projection projection;

    public StructurePoolCreatedEvent(ResourceLocation createdPoolName, List<Pair<StructurePoolElement, Integer>> rawTemplates, List<StructurePoolElement> templates, StructureTemplatePool.Projection projection) {
        this.createdPoolName = createdPoolName;
        this.rawTemplates = rawTemplates;
        this.templates = templates;
        this.projection = projection;
    }

    /**
     * @param function a function returning a StructurePoolElement, usually obtained by {@link net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement#legacy(String)}
     * @param weight   the spawn weight of the given structure for the current pool (higher number = more frequent spawning)
     */
    public void addTemplate(Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> function, int weight) {
        StructurePoolElement structurePoolElement = function.apply(this.projection);
        this.rawTemplates.add(Pair.of(structurePoolElement, weight));

        for(int i = 0; i < weight; ++i) {
            this.templates.add(structurePoolElement);
        }
    }

    /**
     * @return The {@link net.minecraft.resources.ResourceLocation} of the pool being created
     */
    public ResourceLocation getCreatedPoolName() {
        return createdPoolName;
    }

}
