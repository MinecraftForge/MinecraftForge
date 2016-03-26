package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableSet;

/**
 * Created by rainwarrior on 2/2/16.
 */
public interface IModelWithSubmodels<M extends IModelWithSubmodels<M>> extends IModel
{
    M setDefaultSubmodelEnabled (boolean value);
    M setSubmodelsEnabled(ImmutableSet<String> submodelNames, boolean show);
}
