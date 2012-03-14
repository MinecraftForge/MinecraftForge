package argo.jdom;

public final class JsonNodeSelector
{
    final Functor valueGetter;

    JsonNodeSelector(Functor par1Functor)
    {
        this.valueGetter = par1Functor;
    }

    /**
     * Determines whether this JsonNodeSelector can extract a value from the given JsonNode
     */
    public boolean matches(Object par1Obj)
    {
        return this.valueGetter.matchesNode(par1Obj);
    }

    public Object getValue(Object par1Obj)
    {
        return this.valueGetter.applyTo(par1Obj);
    }

    /**
     * Constructs a JsonNodeSelector consisting of this chained with the given JsonNodeSelector. For example, if we have
     * JsonNodeSelectors for the first element of an array, and another that selects the second element of an array, and
     * we chain them together in that order, we will get a selector that works on nested arrays, selecting the second
     * element from an array stored in the first element of a parent array
     */
    public JsonNodeSelector with(JsonNodeSelector par1JsonNodeSelector)
    {
        return new JsonNodeSelector(new ChainedFunctor(this, par1JsonNodeSelector));
    }

    String shortForm()
    {
        return this.valueGetter.shortForm();
    }

    public String toString()
    {
        return this.valueGetter.toString();
    }
}
