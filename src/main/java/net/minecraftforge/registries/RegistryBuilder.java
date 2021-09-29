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

package net.minecraftforge.registries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry.*;

import javax.annotation.Nullable;

public class RegistryBuilder<T extends IForgeRegistryEntry<T>>
{
    private static final int MAX_ID = Integer.MAX_VALUE - 1;

    private ResourceLocation registryName;
    private Class<T> registryType;
    private ResourceLocation optionalDefaultKey;
    private int minId = 0;
    private int maxId = MAX_ID;
    private List<AddCallback<T>> addCallback = Lists.newArrayList();
    private List<ClearCallback<T>> clearCallback = Lists.newArrayList();
    private List<CreateCallback<T>> createCallback = Lists.newArrayList();
    private List<ValidateCallback<T>> validateCallback = Lists.newArrayList();
    private List<BakeCallback<T>> bakeCallback = Lists.newArrayList();
    private boolean saveToDisc = true;
    private boolean sync = true;
    private boolean allowOverrides = true;
    private boolean allowModifications = false;
    private boolean hasWrapper = false;
    @Nullable
    private String tagFolder;
    private DummyFactory<T> dummyFactory;
    private MissingFactory<T> missingFactory;
    private Set<ResourceLocation> legacyNames = new HashSet<>();

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
        this.minId = Math.max(min, 0);
        this.maxId = Math.min(max, MAX_ID);
        return this;
    }

    public RegistryBuilder<T> setMaxID(int max)
    {
        return this.setIDRange(0, max);
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
        if (inst instanceof ValidateCallback)
            this.add((ValidateCallback<T>)inst);
        if (inst instanceof BakeCallback)
            this.add((BakeCallback<T>)inst);
        if (inst instanceof DummyFactory)
            this.set((DummyFactory<T>)inst);
        if (inst instanceof MissingFactory)
            this.set((MissingFactory<T>)inst);
        return this;
    }

    public RegistryBuilder<T> add(AddCallback<T> add)
    {
        this.addCallback.add(add);
        return this;
    }

    public RegistryBuilder<T> onAdd(AddCallback<T> add)
    {
        return this.add(add);
    }

    public RegistryBuilder<T> add(ClearCallback<T> clear)
    {
        this.clearCallback.add(clear);
        return this;
    }

    public RegistryBuilder<T> onClear(ClearCallback<T> clear)
    {
        return this.add(clear);
    }

    public RegistryBuilder<T> add(CreateCallback<T> create)
    {
        this.createCallback.add(create);
        return this;
    }

    public RegistryBuilder<T> onCreate(CreateCallback<T> create)
    {
        return this.add(create);
    }

    public RegistryBuilder<T> add(ValidateCallback<T> validate)
    {
        this.validateCallback.add(validate);
        return this;
    }

    public RegistryBuilder<T> onValidate(ValidateCallback<T> validate)
    {
        return this.add(validate);
    }

    public RegistryBuilder<T> add(BakeCallback<T> bake)
    {
        this.bakeCallback.add(bake);
        return this;
    }

    public RegistryBuilder<T> onBake(BakeCallback<T> bake)
    {
        return this.add(bake);
    }

    public RegistryBuilder<T> set(DummyFactory<T> factory)
    {
        this.dummyFactory = factory;
        return this;
    }

    public RegistryBuilder<T> dummy(DummyFactory<T> factory)
    {
        return this.set(factory);
    }

    public RegistryBuilder<T> set(MissingFactory<T> missing)
    {
        this.missingFactory = missing;
        return this;
    }

    public RegistryBuilder<T> missing(MissingFactory<T> missing)
    {
        return this.set(missing);
    }

    public RegistryBuilder<T> disableSaving()
    {
        this.saveToDisc = false;
        return this;
    }

    public RegistryBuilder<T> disableSync()
    {
        this.sync = false;
        return this;
    }

    public RegistryBuilder<T> disableOverrides()
    {
        this.allowOverrides = false;
        return this;
    }

    public RegistryBuilder<T> allowModification()
    {
        this.allowModifications = true;
        return this;
    }

    RegistryBuilder<T> hasWrapper()
    {
        this.hasWrapper = true;
        return this;
    }

    public RegistryBuilder<T> tagFolder(String tagFolder)
    {
        if (tagFolder == null || !tagFolder.matches("[a-z_/]+")) throw new IllegalArgumentException("Non [a-z_/] character in tag folder " + tagFolder);
        this.tagFolder = tagFolder;
        //Also mark this registry as having a wrapper to a vanilla registry so that it can be used in data generators properly
        hasWrapper();
        return this;
    }

    public RegistryBuilder<T> legacyName(String name)
    {
        return legacyName(new ResourceLocation(name));
    }

    public RegistryBuilder<T> legacyName(ResourceLocation name)
    {
        this.legacyNames.add(name);
        return this;
    }

    public IForgeRegistry<T> create()
    {
        if (hasWrapper)
        {
            if (getDefault() == null)
                addCallback(new NamespacedWrapper.Factory<T>());
            else
                addCallback(new NamespacedDefaultedWrapper.Factory<T>());
        }
        return RegistryManager.ACTIVE.createRegistry(registryName, this);
    }

    @Nullable
    public AddCallback<T> getAdd()
    {
        if (addCallback.isEmpty())
            return null;
        if (addCallback.size() == 1)
            return addCallback.get(0);

        return (owner, stage, id, obj, old) ->
        {
            for (AddCallback<T> cb : this.addCallback)
                cb.onAdd(owner, stage, id, obj, old);
        };
    }

    @Nullable
    public ClearCallback<T> getClear()
    {
        if (clearCallback.isEmpty())
            return null;
        if (clearCallback.size() == 1)
            return clearCallback.get(0);

        return (owner, stage) ->
        {
            for (ClearCallback<T> cb : this.clearCallback)
                cb.onClear(owner, stage);
        };
    }

    @Nullable
    public CreateCallback<T> getCreate()
    {
        if (createCallback.isEmpty())
            return null;
        if (createCallback.size() == 1)
            return createCallback.get(0);

        return (owner, stage) ->
        {
            for (CreateCallback<T> cb : this.createCallback)
                cb.onCreate(owner, stage);
        };
    }

    @Nullable
    public ValidateCallback<T> getValidate()
    {
        if (validateCallback.isEmpty())
            return null;
        if (validateCallback.size() == 1)
            return validateCallback.get(0);

        return (owner, stage, id, key, obj) ->
        {
            for (ValidateCallback<T> cb : this.validateCallback)
                cb.onValidate(owner, stage, id, key, obj);
        };
    }

    @Nullable
    public BakeCallback<T> getBake()
    {
        if (bakeCallback.isEmpty())
            return null;
        if (bakeCallback.size() == 1)
            return bakeCallback.get(0);

        return (owner, stage) ->
        {
            for (BakeCallback<T> cb : this.bakeCallback)
                cb.onBake(owner, stage);
        };
    }

    public Class<T> getType()
    {
        return registryType;
    }

    @Nullable
    public ResourceLocation getDefault()
    {
        return this.optionalDefaultKey;
    }

    public int getMinId()
    {
        return minId;
    }

    public int getMaxId()
    {
        return maxId;
    }

    public boolean getAllowOverrides()
    {
        return allowOverrides;
    }

    public boolean getAllowModifications()
    {
        return allowModifications;
    }

    @Nullable
    public String getTagFolder()
    {
        return tagFolder;
    }

    @Nullable
    public DummyFactory<T> getDummyFactory()
    {
        return dummyFactory;
    }

    @Nullable
    public MissingFactory<T> getMissingFactory()
    {
        return missingFactory;
    }

    public boolean getSaveToDisc()
    {
        return saveToDisc;
    }

    public boolean getSync()
    {
        return sync;
    }

    public Set<ResourceLocation> getLegacyNames()
    {
        return legacyNames;
    }
}
