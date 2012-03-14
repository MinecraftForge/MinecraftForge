package argo.jdom;

import java.util.Map;

final class JsonNodeSelectors_Field extends LeafFunctor
{
    final JsonStringNode field_27066_a;

    JsonNodeSelectors_Field(JsonStringNode par1JsonStringNode)
    {
        this.field_27066_a = par1JsonStringNode;
    }

    public boolean func_27065_a(Map par1Map)
    {
        return par1Map.containsKey(this.field_27066_a);
    }

    public String shortForm()
    {
        return "\"" + this.field_27066_a.getText() + "\"";
    }

    public JsonNode func_27064_b(Map par1Map)
    {
        return (JsonNode)par1Map.get(this.field_27066_a);
    }

    public String toString()
    {
        return "a field called [\"" + this.field_27066_a.getText() + "\"]";
    }

    public Object typeSafeApplyTo(Object par1Obj)
    {
        return this.func_27064_b((Map)par1Obj);
    }

    public boolean matchesNode(Object par1Obj)
    {
        return this.func_27065_a((Map)par1Obj);
    }
}
