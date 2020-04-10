/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.common.discovery;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.lang3.tuple.Pair;

public class ASMDataTable
{
    public final static class ASMData implements Cloneable
    {
        private ModCandidate candidate;
        private String annotationName;
        private String className;
        private String objectName;
        private int classVersion;
        private Map<String,Object> annotationInfo;
        public ASMData(ModCandidate candidate, String annotationName, String className, @Nullable String objectName, @Nullable Map<String,Object> info)
        {
            this.candidate = candidate;
            this.annotationName = annotationName;
            this.className = className;
            this.objectName = objectName;
            this.annotationInfo = info;
        }
        public ModCandidate getCandidate()
        {
            return candidate;
        }
        public String getAnnotationName()
        {
            return annotationName;
        }
        public String getClassName()
        {
            return className;
        }
        public String getObjectName()
        {
            return objectName;
        }
        public Map<String, Object> getAnnotationInfo()
        {
            return annotationInfo;
        }

        public ASMData copy(Map<String,Object> newAnnotationInfo)
        {
            try
            {
                ASMData clone = (ASMData) this.clone();
                clone.annotationInfo = newAnnotationInfo;
                return clone;
            }
            catch (CloneNotSupportedException e)
            {
                throw new RuntimeException("Unpossible", e);
            }
        }
    }

    private static class ModContainerPredicate implements Predicate<ASMData>
    {
        private ModContainer container;
        public ModContainerPredicate(ModContainer container)
        {
            this.container = container;
        }
        @Override
        public boolean apply(ASMData data)
        {
            return container.getSource().equals(data.candidate.getModContainer());
        }
    }
    private final SetMultimap<String, ASMData> globalAnnotationData = HashMultimap.create();
    private Map<ModContainer, SetMultimap<String,ASMData>> containerAnnotationData;

    private List<ModContainer> containers = Lists.newArrayList();
    private SetMultimap<String,ModCandidate> packageMap = HashMultimap.create();

    public SetMultimap<String,ASMData> getAnnotationsFor(ModContainer container)
    {
        if (containerAnnotationData == null)
        {
            //concurrently filter the values to speed this up
            containerAnnotationData = containers.parallelStream()
                    .map(cont -> Pair.of(cont, ImmutableSetMultimap.copyOf(Multimaps.filterValues(globalAnnotationData, new ModContainerPredicate(cont)))))
                    .collect(ImmutableMap.toImmutableMap(Pair::getKey, Pair::getValue));
        }
        return containerAnnotationData.get(container);
    }

    public Set<ASMData> getAll(String annotation)
    {
        return globalAnnotationData.get(annotation);
    }

    public void addASMData(ModCandidate candidate, String annotation, String className, @Nullable String objectName, @Nullable Map<String,Object> annotationInfo)
    {
        globalAnnotationData.put(annotation, new ASMData(candidate, annotation, className, objectName, annotationInfo));
    }

    public void addContainer(ModContainer container)
    {
        this.containers.add(container);
    }

    public void registerPackage(ModCandidate modCandidate, String pkg)
    {
        this.packageMap.put(pkg,modCandidate);
    }

    public Set<ModCandidate> getCandidatesFor(String pkg)
    {
        return this.packageMap.get(pkg);
    }

    @Nullable
    public static String getOwnerModID(Set<ASMData> mods, ASMData targ)
    {
        if (mods.size() == 1) {
            return (String)mods.iterator().next().getAnnotationInfo().get("modid");
        } else {
            for (ASMData m : mods) {
                if (targ.getClassName().startsWith(m.getClassName())) {
                    return (String)m.getAnnotationInfo().get("modid");
                }
            }
        }
        return null;
    }
}
