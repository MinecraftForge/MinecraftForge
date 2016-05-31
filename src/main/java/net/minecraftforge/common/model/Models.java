package net.minecraftforge.common.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

public enum Models
{
    ;

    public static IModelPart getHiddenModelPart(ImmutableList<String> path)
    {
        return new HiddenModelPart(path);
    }

    public static UnmodifiableIterator<String> getParts(IModelPart part)
    {
        if(part instanceof HiddenModelPart)
        {
            return ((HiddenModelPart) part).getPath().iterator();
        }
        return Iterators.emptyIterator();
    }
}
