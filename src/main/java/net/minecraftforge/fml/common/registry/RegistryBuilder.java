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

package net.minecraftforge.fml.common.registry;

import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.IForgeRegistry.*;

public class RegistryBuilder<T extends IForgeRegistryEntry<T>>
{
    private ResourceLocation registryName;
    private Class<T> registryType;
    private ResourceLocation optionalDefaultKey;
    private int minId;
    private int maxId;
    private List<AddCallback<T>> addCallback = Lists.newArrayList();
    private List<ClearCallback<T>> clearCallback = Lists.newArrayList();
    private List<CreateCallback<T>> createCallback = Lists.newArrayList();
    private List<SubstitutionCallback<T>> substitutionCallback = Lists.newArrayList();

    public RegistryBuilder<T> setName(ResourceLocation name)
    {
        this.registryName = name;
        return this;
    }

    public RegistryBuilder<T> setType(Class<T> type)
    {
        this.registryType = type;
        return this;
    }

    public RegistryBuilder<T> setIDRange(int min, int max)
    {
        this.minId = min;
        this.maxId = max;
        return this;
    }

    public RegistryBuilder<T> setDefaultKey(ResourceLocation key)
    {
        this.optionalDefaultKey = key;
        return this;
    }

    @SuppressWarnings("unchecked")
    public RegistryBuilder<T> addCallback(Object inst)
    {
        if (inst instanceof AddCallback)
            this.add((AddCallback<T>)inst);
        if (inst instanceof ClearCallback)
            this.add((ClearCallback<T>)inst);
        if (inst instanceof CreateCallback)
            this.add((CreateCallback<T>)inst);
        if (inst instanceof SubstitutionCallback)
            this.add((SubstitutionCallback<T>)inst);
        return this;
    }

    public RegistryBuilder<T> add(AddCallback<T> add)
    {
        this.addCallback.add(add);
        return this;
    }

    public RegistryBuilder<T> add(ClearCallback<T> clear)
    {
        this.clearCallback.add(clear);
        return this;
    }

    public RegistryBuilder<T> add(CreateCallback<T> create)
    {
        this.createCallback.add(create);
        return this;
    }

    public RegistryBuilder<T> add(SubstitutionCallback<T> sub)
    {
        this.substitutionCallback.add(sub);
        return this;
    }

    @SuppressWarnings("deprecation")
    public IForgeRegistry<T> create()
    {
        return PersistentRegistryManager.createRegistry(registryName, registryType, optionalDefaultKey, minId, maxId, false,
                getAdd(), getClear(), getCreate(), getSubstitution());
    }

    private AddCallback<T> getAdd()
    {
        if (this.addCallback.isEmpty())
            return null;
        if (this.addCallback.size() == 1)
            return this.addCallback.get(0);

        return new AddCallback<T>()
        {
            @Override
            public void onAdd(T obj, int id, Map<ResourceLocation, ?> slaveset)
            {
                for (AddCallback<T> cb : RegistryBuilder.this.addCallback)
                    cb.onAdd(obj, id, slaveset);
            }
        };
    }
    private ClearCallback<T> getClear()
    {
        if (this.clearCallback.isEmpty())
            return null;
        if (this.clearCallback.size() == 1)
            return this.clearCallback.get(0);

        return new ClearCallback<T>()
        {
            @Override
            public void onClear(IForgeRegistry<T> is, Map<ResourceLocation, ?> slaveset)
            {
                for (ClearCallback<T> cb : RegistryBuilder.this.clearCallback)
                    cb.onClear(is, slaveset);
            }
        };
    }
    private CreateCallback<T> getCreate()
    {
        if (this.createCallback.isEmpty())
            return null;
        if (this.createCallback.size() == 1)
            return this.createCallback.get(0);

        return new CreateCallback<T>()
        {
            @Override
            public void onCreate(Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries)
            {
                for (CreateCallback<T> cb : RegistryBuilder.this.createCallback)
                    cb.onCreate(slaveset, registries);
            }
        };
    }
    private SubstitutionCallback<T> getSubstitution()
    {
        if (this.substitutionCallback.isEmpty())
            return null;
        if (this.substitutionCallback.size() == 1)
            return this.substitutionCallback.get(0);

        return new SubstitutionCallback<T>()
        {
            @Override
            public void onSubstituteActivated(Map<ResourceLocation, ?> slaveset, T original, T replacement, ResourceLocation name)
            {
                for (SubstitutionCallback<T> cb : RegistryBuilder.this.substitutionCallback)
                    cb.onSubstituteActivated(slaveset, original, replacement, name);
            }
        };
    }
}
