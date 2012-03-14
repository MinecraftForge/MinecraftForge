package argo.jdom;

final class ChainedFunctor implements Functor
{
    private final JsonNodeSelector parentJsonNodeSelector;
    private final JsonNodeSelector childJsonNodeSelector;

    ChainedFunctor(JsonNodeSelector par1JsonNodeSelector, JsonNodeSelector par2JsonNodeSelector)
    {
        this.parentJsonNodeSelector = par1JsonNodeSelector;
        this.childJsonNodeSelector = par2JsonNodeSelector;
    }

    public boolean matchesNode(Object par1Obj)
    {
        return this.parentJsonNodeSelector.matches(par1Obj) && this.childJsonNodeSelector.matches(this.parentJsonNodeSelector.getValue(par1Obj));
    }

    public Object applyTo(Object par1Obj)
    {
        Object var2;

        try
        {
            var2 = this.parentJsonNodeSelector.getValue(par1Obj);
        }
        catch (JsonNodeDoesNotMatchChainedJsonNodeSelectorException var6)
        {
            throw JsonNodeDoesNotMatchChainedJsonNodeSelectorException.func_27321_b(var6, this.parentJsonNodeSelector);
        }

        try
        {
            Object var3 = this.childJsonNodeSelector.getValue(var2);
            return var3;
        }
        catch (JsonNodeDoesNotMatchChainedJsonNodeSelectorException var5)
        {
            throw JsonNodeDoesNotMatchChainedJsonNodeSelectorException.func_27323_a(var5, this.parentJsonNodeSelector);
        }
    }

    public String shortForm()
    {
        return this.childJsonNodeSelector.shortForm();
    }

    public String toString()
    {
        return this.parentJsonNodeSelector.toString() + ", with " + this.childJsonNodeSelector.toString();
    }
}
