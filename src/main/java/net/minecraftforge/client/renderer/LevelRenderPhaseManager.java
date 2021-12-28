package net.minecraftforge.client.renderer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.LayerRenderTypeRegisterEvent;
import net.minecraftforge.common.util.SortedRegistry;
import net.minecraftforge.fml.ModLoader;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Manager which deals with rendering phases used during level rendering.
 * This class is only in use when the {@link LevelRendererAdapter} is used.
 */
public final class LevelRenderPhaseManager
{
    private static final LevelRenderPhaseManager INSTANCE = new LevelRenderPhaseManager();

    public static LevelRenderPhaseManager getInstance()
    {
        return INSTANCE;
    }

    private SortedRegistry<RenderType> solidPhases       = null;
    private SortedRegistry<RenderType> translucentPhases = null;
    private SortedRegistry<RenderType> tripwirePhases    = null;

    private final BiMap<ResourceLocation, RenderType> allKnownTypes = HashBiMap.create();
    private final BiMap<ResourceLocation, RenderType> allKnownTypesView = Maps.unmodifiableBiMap(allKnownTypes);

    private final List<RenderType> knownTypes = Lists.newArrayList();
    private final List<RenderType> knownTypesView = Collections.unmodifiableList(knownTypes);

    private LevelRenderPhaseManager()
    {
    }

    public void init()
    {
        if (solidPhases != null)
            throw new IllegalStateException("Attempted to set up static render types twice.");

        var solidBuilder = new SortedRegistry.Builder<>(RenderType.class);
        var translucentBuilder = new SortedRegistry.Builder<>(RenderType.class);
        var tripwireBuilder = new SortedRegistry.Builder<>(RenderType.class);

        addSolidVanillaTypes(solidBuilder);
        addTranslucentVanillaTypes(translucentBuilder);
        addTripwireVanillaTypes(tripwireBuilder);

        ModLoader.get().postEvent(new LayerRenderTypeRegisterEvent.Solid(solidBuilder));
        ModLoader.get().postEvent(new LayerRenderTypeRegisterEvent.Translucent(translucentBuilder));
        ModLoader.get().postEvent(new LayerRenderTypeRegisterEvent.Tripwire(tripwireBuilder));

        solidPhases = solidBuilder.build();
        translucentPhases = translucentBuilder.build();
        tripwirePhases = tripwireBuilder.build();

        allKnownTypes.clear();
        allKnownTypes.putAll(
          solidPhases.getUnorderedEntries()
        );
        allKnownTypes.putAll(
          translucentPhases.getUnorderedEntries()
        );
        allKnownTypes.putAll(
          tripwirePhases.getUnorderedEntries()
        );

        knownTypes.clear();
        knownTypes.addAll(allKnownTypes.values());
    }

    public Collection<RenderType> getSolidPhases()
    {
        return solidPhases.getElements();
    }

    public Collection<RenderType> getTranslucentPhases()
    {
        return translucentPhases.getElements();
    }

    public Collection<RenderType> getTripwirePhases()
    {
        return tripwirePhases.getElements();
    }

    public BiMap<ResourceLocation, RenderType> getAllKnownTypes()
    {
        return allKnownTypesView;
    }

    public List<RenderType> getKnownTypes()
    {
        return knownTypesView;
    }

    private static void addSolidVanillaTypes(SortedRegistry.Builder<RenderType> solidBuilder)
    {
        var solid = new ResourceLocation("solid");
        var cutoutMipped = new ResourceLocation("cutout_mipped");
        var cutout = new ResourceLocation("cutout");
        solidBuilder.add(RenderType.solid(), solid, List.of(), List.of(cutoutMipped));
        solidBuilder.add(RenderType.cutoutMipped(), cutoutMipped, List.of(solid), List.of(cutout));
        solidBuilder.add(RenderType.cutout(), cutout, List.of(cutoutMipped), List.of());
    }

    private static void addTranslucentVanillaTypes(SortedRegistry.Builder<RenderType> translucentBuilder)
    {
        var translucent = new ResourceLocation("translucent");
        translucentBuilder.add(RenderType.translucent(), translucent, List.of(), List.of());
    }

    private static void addTripwireVanillaTypes(SortedRegistry.Builder<RenderType> tripwireBuilder)
    {
        var tripwire = new ResourceLocation("tripwire");
        tripwireBuilder.add(RenderType.tripwire(), tripwire, List.of(), List.of());
    }
}
