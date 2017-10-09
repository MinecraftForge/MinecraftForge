/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
package net.minecraftforge.unification;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a set of interchangeable things, with one chosen to be the 'unified' variant.
 * It is defined by a {@link UnifiedMaterial}, a {@link UnifiedForm}, and a list of variants.
 *
 * To get an instance, add your own variant in preInit with {@link Unifier#add(UnifiedMaterial, UnifiedForm, T)},
 * and then retrieve it after with {@link Unifier#get(UnifiedMaterial, UnifiedForm)}.
 *
 * One variant is selected to be "the one" which is output from {@link #get()}.
 * The variant that gets selected can be manipulated by the Forge Config, see "forge.configgui.unificationRegistryPriorityModIds"
 */
public final class Unified<T> implements Supplier<T>
{
    private final SortedSet<T> variants;
    private final UnifiedMaterial material;
    private final UnifiedForm form;
    private final Function<T, T> copier;

    /**
     * Create an instance by using {@link Unifier#add(UnifiedMaterial, UnifiedForm, T)}
     * and then get it with {@link Unifier#get(UnifiedMaterial, UnifiedForm)}
     */
    Unified(UnifiedMaterial material, UnifiedForm form, Comparator<T> sortOrder, Function<T, T> copier)
    {
        this.variants = new TreeSet<>(sortOrder);
        this.material = material;
        this.form = form;
        this.copier = copier;
    }

    /**
     * @return the chosen 'unified' variant
     */
    @Override
    public T get()
    {
        T variant = variants.first();
        return copier.apply(variant);
    }

    /**
     * @return all the variants that have been registered
     */
    public Collection<T> getVariants()
    {
        return Collections.unmodifiableSet(variants);
    }

    /**
     * @return the {@link UnifiedMaterial} property
     */
    public UnifiedMaterial getMaterial()
    {
        return material;
    }

    /**
     * @return the {@link UnifiedForm} property
     */
    public UnifiedForm getForm()
    {
        return form;
    }

    /**
     * Use {@link Unifier#add(UnifiedMaterial, UnifiedForm, T)} to add a variant
     */
    void addVariant(T variant)
    {
        this.variants.add(variant);
    }

    @Override
    public String toString()
    {
        return String.format("%s %s: %s", getMaterial(), getForm(), get());
    }
}
