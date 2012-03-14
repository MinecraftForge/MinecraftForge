package argo.jdom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class JsonArray extends JsonRootNode
{
    private final List elements;

    JsonArray(Iterable par1Iterable)
    {
        this.elements = asList(par1Iterable);
    }

    public JsonNodeType getType()
    {
        return JsonNodeType.ARRAY;
    }

    public List getElements()
    {
        return new ArrayList(this.elements);
    }

    public String getText()
    {
        throw new IllegalStateException("Attempt to get text on a JsonNode without text.");
    }

    /**
     * return the fields associated with this node
     */
    public Map getFields()
    {
        throw new IllegalStateException("Attempt to get fields on a JsonNode without fields.");
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (par1Obj != null && this.getClass() == par1Obj.getClass())
        {
            JsonArray var2 = (JsonArray)par1Obj;
            return this.elements.equals(var2.elements);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.elements.hashCode();
    }

    public String toString()
    {
        return "JsonArray elements:[" + this.elements + "]";
    }

    private static List asList(Iterable par0Iterable)
    {
        return new JsonArray_NodeList(par0Iterable);
    }
}
