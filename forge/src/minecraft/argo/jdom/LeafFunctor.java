package argo.jdom;

abstract class LeafFunctor implements Functor
{
    public final Object applyTo(Object par1Obj)
    {
        if (!this.matchesNode(par1Obj))
        {
            throw JsonNodeDoesNotMatchChainedJsonNodeSelectorException.func_27322_a(this);
        }
        else
        {
            return this.typeSafeApplyTo(par1Obj);
        }
    }

    protected abstract Object typeSafeApplyTo(Object var1);
}
