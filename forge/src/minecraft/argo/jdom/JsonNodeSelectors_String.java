package argo.jdom;

final class JsonNodeSelectors_String extends LeafFunctor
{
    public boolean func_27072_a(JsonNode par1JsonNode)
    {
        return JsonNodeType.STRING == par1JsonNode.getType();
    }

    public String shortForm()
    {
        return "A short form string";
    }

    public String func_27073_b(JsonNode par1JsonNode)
    {
        return par1JsonNode.getText();
    }

    public String toString()
    {
        return "a value that is a string";
    }

    public Object typeSafeApplyTo(Object par1Obj)
    {
        return this.func_27073_b((JsonNode)par1Obj);
    }

    public boolean matchesNode(Object par1Obj)
    {
        return this.func_27072_a((JsonNode)par1Obj);
    }
}
