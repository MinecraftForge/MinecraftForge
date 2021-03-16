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

package net.minecraftforge.common;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * New implementation of {@link net.minecraft.advancements.PlayerAdvancements#ensureAllVisible()}
 */
public class AdvancementLoadFix {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Map<Advancement, List<Advancement>> roots;

    public static void loadVisibility(final PlayerAdvancements playerAdvancements, final Set<Advancement> visible, final Set<Advancement> visibilityChanged, final Map<Advancement, AdvancementProgress> progress, final Set<Advancement> progressChanged, final Predicate<Advancement> shouldBeVisible) {
        LOGGER.info("Using new advancement loading for {}", playerAdvancements);
        if (roots == null) throw new RuntimeException("Why did the advancements not load yet?!");
        final Set<Advancement> set = new HashSet<>();
        for(Map.Entry<Advancement, AdvancementProgress> entry : progress.entrySet()) {
            if (entry.getValue().isDone()) {
                set.add(entry.getKey());
                progressChanged.add(entry.getKey());
            }
        }

        roots.values().stream().flatMap(Collection::stream).filter(adv -> containsAncestor(adv, set)).forEach(adv->updateVisibility(adv, visible, visibilityChanged, progress, progressChanged, shouldBeVisible));
    }

    private static boolean containsAncestor(final Advancement adv, final Set<Advancement> set) {
        return set.contains(adv) || (adv.getParent() != null && containsAncestor(adv.getParent(), set));
    }

    private static void updateVisibility(final Advancement adv, final Set<Advancement> visible, final Set<Advancement> visibilityChanged, final Map<Advancement, AdvancementProgress> progress, final Set<Advancement> progressChanged, Predicate<Advancement> shouldBeVisible) {
        boolean flag = shouldBeVisible.test(adv);
        boolean flag1 = visible.contains(adv);
        if (flag && !flag1) {
            visible.add(adv);
            visibilityChanged.add(adv);
            if (progress.containsKey(adv)) {
                progressChanged.add(adv);
            }
        } else if (!flag && flag1) {
            visible.remove(adv);
            visibilityChanged.add(adv);
        }
        if (flag!=flag1 && adv.getParent()!=null)
            updateVisibility(adv.getParent(), visible, visibilityChanged, progress, progressChanged, shouldBeVisible);
    }

    public static void buildSortedTrees(final Set<Advancement> roots) {
        AdvancementLoadFix.roots = roots.stream()
                .map(AdvancementLoadFix::buildGraph)
                .map(g -> TopologicalSort.topologicalSort(g, Comparator.comparing(Advancement::getId)))
                .collect(Collectors.toMap(lst -> lst.get(0), Function.identity()));
    }

    private static Graph<Advancement> buildGraph(final Advancement root) {
        final MutableGraph<Advancement> tree = GraphBuilder.directed().build();
        addEdgesAndChildren(root, tree);
        return tree;
    }

    private static void addEdgesAndChildren(final Advancement root, final MutableGraph<Advancement> tree) {
        tree.addNode(root);
        for (Advancement adv: root.getChildren()) {
            addEdgesAndChildren(adv, tree);
            tree.putEdge(root, adv);
        }
    }
}
