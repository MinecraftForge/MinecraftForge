/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import cpw.mods.modlauncher.util.ServiceLoaderUtils;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class ModStateManager {
    static ModStateManager INSTANCE;
    private final EnumMap<ModLoadingPhase, List<IModLoadingState>> stateMap;

    public ModStateManager() {
        INSTANCE = this;
        final var sp = ServiceLoader.load(FMLLoader.getGameLayer(), IModStateProvider.class);
        this.stateMap = ServiceLoaderUtils.streamWithErrorHandling(sp, sce->{})
                .map(IModStateProvider::getAllStates)
                .<IModLoadingState>mapMulti(Iterable::forEach)
                .collect(Collectors.groupingBy(IModLoadingState::phase, ()->new EnumMap<>(ModLoadingPhase.class), Collectors.toUnmodifiableList()));
    }

    public List<IModLoadingState> getStates(final ModLoadingPhase phase) {
        var nodes = stateMap.get(phase);
        var lookup = nodes.stream().collect(Collectors.toMap(IModLoadingState::name, Function.identity()));

        var graph = GraphBuilder.directed().allowsSelfLoops(false).<IModLoadingState>build();
        var dummy = ModLoadingState.empty("", "", phase);
        nodes.forEach(graph::addNode);
        graph.addNode(dummy);
        nodes.forEach(n->graph.putEdge(lookup.getOrDefault(n.previous(), dummy), n));
        return TopologicalSort.topologicalSort(graph, Comparator.comparingInt(nodes::indexOf)).stream().filter(st->st!=dummy).toList();
    }
}
