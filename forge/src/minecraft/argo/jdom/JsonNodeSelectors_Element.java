package argo.jdom;

import java.util.List;

final class JsonNodeSelectors_Element extends LeafFunctor
{
    final int index;

    JsonNodeSelectors_Element(int par1)
    {
        this.index = par1;
    }

    public boolean matchesNode_(List par1List)
    {
        return par1List.size() > this.index;
    }

    public String shortForm()
    {
        return Integer.toString(this.index);
    }

    public JsonNode typeSafeApplyTo_(List par1List)
    {
        return (JsonNode)par1List.get(this.index);
    }

    public String toString()
    {
        return "an element at index [" + this.index + "]";
    }

    public Object typeSafeApplyTo(Object par1Obj)
    {
        return this.typeSafeApplyTo_((List)par1Obj);
    }

    public boolean matchesNode(Object par1Obj)
    {
        return this.matchesNode_((List)par1Obj);
    }
}
