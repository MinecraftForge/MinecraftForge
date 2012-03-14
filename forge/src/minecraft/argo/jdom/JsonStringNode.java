package argo.jdom;

import java.util.List;
import java.util.Map;

public final class JsonStringNode extends JsonNode implements Comparable
{
    private final String value;

    JsonStringNode(String par1Str)
    {
        if (par1Str == null)
        {
            throw new NullPointerException("Attempt to construct a JsonString with a null value.");
        }
        else
        {
            this.value = par1Str;
        }
    }

    public JsonNodeType getType()
    {
        return JsonNodeType.STRING;
    }

    public String getText()
    {
        return this.value;
    }

    /**
     * return the fields associated with this node
     */
    public Map getFields()
    {
        throw new IllegalStateException("Attempt to get fields on a JsonNode without fields.");
    }

    public List getElements()
    {
        throw new IllegalStateException("Attempt to get elements on a JsonNode without elements.");
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (par1Obj != null && this.getClass() == par1Obj.getClass())
        {
            JsonStringNode var2 = (JsonStringNode)par1Obj;
            return this.value.equals(var2.value);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.value.hashCode();
    }

    public String toString()
    {
        return "JsonStringNode value:[" + this.value + "]";
    }

    public int func_27223_a(JsonStringNode par1JsonStringNode)
    {
        return this.value.compareTo(par1JsonStringNode.value);
    }

    public int compareTo(Object par1Obj)
    {
        return this.func_27223_a((JsonStringNode)par1Obj);
    }
}
